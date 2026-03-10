package com.robsartin.maintainly.application.web;

import java.security.Principal;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import com.robsartin.maintainly.domain.model.AppUser;
import com.robsartin.maintainly.domain.model.FrequencyUnit;
import com.robsartin.maintainly.domain.model.Item;
import com.robsartin.maintainly.domain.model.ServiceRecord;
import com.robsartin.maintainly.domain.model.ServiceSchedule;
import com.robsartin.maintainly.domain.model.ServiceType;
import com.robsartin.maintainly.domain.model.Vendor;
import com.robsartin.maintainly.domain.port.in.UserResolver;
import com.robsartin.maintainly.domain.port.out.ItemRepository;
import com.robsartin.maintainly.domain.port.out.ServiceRecordRepository;
import com.robsartin.maintainly.domain.port.out.ServiceScheduleRepository;
import com.robsartin.maintainly.domain.port.out.ServiceTypeRepository;
import com.robsartin.maintainly.domain.port.out.VendorRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class ItemController {

    private static final Logger log =
            LoggerFactory.getLogger(ItemController.class);
    private static final String MDC_ORG_ID =
            "organizationId";

    private final ItemRepository itemRepository;
    private final ServiceScheduleRepository scheduleRepository;
    private final ServiceRecordRepository recordRepository;
    private final ServiceTypeRepository serviceTypeRepository;
    private final VendorRepository vendorRepository;
    private final UserResolver userResolver;

    public ItemController(
            ItemRepository itemRepository,
            ServiceScheduleRepository scheduleRepository,
            ServiceRecordRepository recordRepository,
            ServiceTypeRepository serviceTypeRepository,
            VendorRepository vendorRepository,
            UserResolver userResolver) {
        this.itemRepository = itemRepository;
        this.scheduleRepository = scheduleRepository;
        this.recordRepository = recordRepository;
        this.serviceTypeRepository = serviceTypeRepository;
        this.vendorRepository = vendorRepository;
        this.userResolver = userResolver;
    }

    @GetMapping("/")
    public String index(
            @RequestParam(required = false) String q,
            Principal principal, Model model) {
        AppUser user = userResolver.resolveOrCreate(
                principal.getName());
        if (!user.hasOrganization()) {
            return handleNoOrganization(user, model);
        }
        UUID orgId = user.getOrganization().getId();
        MDC.put(MDC_ORG_ID, orgId.toString());
        try {
            loadItems(q, orgId, model);
            loadSchedules(orgId, model);
            model.addAttribute("username",
                    user.getUsername());
            model.addAttribute("organization",
                    user.getOrganization());
            return "home";
        } finally {
            MDC.remove(MDC_ORG_ID);
        }
    }

    @GetMapping("/item/detail")
    public String itemDetail(
            @RequestParam UUID itemId,
            Principal principal, Model model) {
        AppUser user = userResolver.resolveOrCreate(
                principal.getName());
        if (!user.hasOrganization()) {
            return handleNoOrganization(user, model);
        }
        UUID orgId = user.getOrganization().getId();
        MDC.put(MDC_ORG_ID, orgId.toString());
        try {
            loadItems(null, orgId, model);
            loadSchedules(orgId, model);
            model.addAttribute("username",
                    user.getUsername());
            model.addAttribute("organization",
                    user.getOrganization());
            model.addAttribute("selectedItemId",
                    itemId);
            model.addAttribute("itemRecords",
                    recordRepository
                            .findByItemIdAndOrganizationId(
                                    itemId, orgId));
            model.addAttribute("itemSchedules",
                    scheduleRepository
                            .findByItemIdAndOrganizationId(
                                    itemId, orgId));
            return "home";
        } finally {
            MDC.remove(MDC_ORG_ID);
        }
    }

    @PostMapping("/item/log")
    public String logItemService(
            @RequestParam UUID itemId,
            @RequestParam String summary,
            @RequestParam String serviceDate,
            @RequestParam(required = false) String techName,
            Principal principal, Model model) {
        AppUser user = userResolver.resolveOrCreate(
                principal.getName());
        setOrgMdc(user);
        try {
            UUID orgId = user.getOrganization().getId();
            Item item = findItem(itemId, orgId);
            saveRecord(orgId, item, null, null,
                    summary, serviceDate, techName);
            return index(null, principal, model);
        } finally {
            MDC.remove(MDC_ORG_ID);
        }
    }

    @PostMapping("/schedule/log")
    public String logScheduleService(
            @RequestParam UUID scheduleId,
            @RequestParam String summary,
            @RequestParam String serviceDate,
            @RequestParam(required = false) String techName,
            Principal principal, Model model) {
        AppUser user = userResolver.resolveOrCreate(
                principal.getName());
        setOrgMdc(user);
        try {
            UUID orgId = user.getOrganization().getId();
            ServiceSchedule sched = findSchedule(
                    scheduleId, orgId);
            LocalDate completed =
                    LocalDate.parse(serviceDate);
            saveRecord(orgId, sched.getItem(),
                    sched.getServiceType(), sched,
                    summary, serviceDate, techName);
            sched.advanceNextDueDate(completed);
            scheduleRepository.save(sched);
            return index(null, principal, model);
        } finally {
            MDC.remove(MDC_ORG_ID);
        }
    }

    @PostMapping("/schedule/delete")
    public String deleteSchedule(
            @RequestParam UUID scheduleId,
            Principal principal, Model model) {
        AppUser user = userResolver.resolveOrCreate(
                principal.getName());
        setOrgMdc(user);
        try {
            UUID orgId = user.getOrganization().getId();
            ServiceSchedule sched = findSchedule(
                    scheduleId, orgId);
            sched.setActive(false);
            scheduleRepository.save(sched);
            log.info("Deactivated schedule {}",
                    scheduleId);
            return index(null, principal, model);
        } finally {
            MDC.remove(MDC_ORG_ID);
        }
    }

    @PostMapping("/item/schedule")
    public String scheduleItemService(
            @RequestParam UUID itemId,
            @RequestParam UUID serviceTypeId,
            @RequestParam String nextDueDate,
            @RequestParam int frequencyInterval,
            @RequestParam FrequencyUnit frequencyUnit,
            @RequestParam(required = false)
                    String vendorId,
            @RequestParam(required = false)
                    String newVendorName,
            @RequestParam(required = false)
                    String newVendorPhone,
            Principal principal, Model model) {
        AppUser user = userResolver.resolveOrCreate(
                principal.getName());
        setOrgMdc(user);
        try {
            UUID orgId = user.getOrganization().getId();
            Item item = findItem(itemId, orgId);
            ServiceType svcType = findServiceType(
                    serviceTypeId, orgId);
            Vendor vendor = resolveVendor(orgId,
                    vendorId, newVendorName,
                    newVendorPhone);
            createSchedule(orgId, item, svcType, vendor,
                    nextDueDate, frequencyInterval,
                    frequencyUnit);
            return index(null, principal, model);
        } finally {
            MDC.remove(MDC_ORG_ID);
        }
    }

    @ExceptionHandler(DateTimeParseException.class)
    public String handleDateParseError(
            DateTimeParseException ex, Model model) {
        log.error("Invalid date format: {}",
                ex.getMessage());
        model.addAttribute("error",
                "Invalid date format: "
                        + ex.getParsedString());
        model.addAttribute("items",
                Collections.emptyList());
        return "home";
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public String handleIllegalArgument(
            IllegalArgumentException ex, Model model) {
        log.error("Invalid argument: {}",
                ex.getMessage());
        model.addAttribute("error", ex.getMessage());
        model.addAttribute("items",
                Collections.emptyList());
        return "home";
    }

    @ExceptionHandler(RuntimeException.class)
    public String handleRuntimeException(
            RuntimeException ex, Model model) {
        log.error("Unexpected error processing request",
                ex);
        model.addAttribute("error",
                "An unexpected error occurred");
        model.addAttribute("items",
                Collections.emptyList());
        return "home";
    }

    private String handleNoOrganization(
            AppUser user, Model model) {
        log.warn("User {} has no organization",
                user.getUsername());
        model.addAttribute("noOrganization", true);
        model.addAttribute("items",
                Collections.emptyList());
        return "home";
    }

    private void loadItems(
            String q, UUID orgId, Model model) {
        List<Item> items;
        if (q != null && !q.isBlank()) {
            log.info("Searching items query={}", q);
            items = itemRepository
                    .searchByOrganizationId(orgId, q);
            model.addAttribute("q", q);
        } else {
            log.info("Listing all items");
            items = itemRepository
                    .findByOrganizationId(orgId);
        }
        model.addAttribute("items", items);
        model.addAttribute("serviceTypes",
                serviceTypeRepository
                        .findByOrganizationId(orgId));
        model.addAttribute("vendors",
                vendorRepository
                        .findByOrganizationId(orgId));
        model.addAttribute("frequencyUnits",
                FrequencyUnit.values());
    }

    private void loadSchedules(UUID orgId, Model model) {
        model.addAttribute("schedules",
                scheduleRepository
                        .findActiveByOrganizationId(
                                orgId));
        LocalDate today = LocalDate.now();
        model.addAttribute("today", today);
        model.addAttribute("soon",
                today.plusWeeks(2));
    }

    private Item findItem(UUID itemId, UUID orgId) {
        return itemRepository
                .findByIdAndOrganizationId(itemId, orgId)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Item not found"));
    }

    private ServiceSchedule findSchedule(
            UUID scheduleId, UUID orgId) {
        return scheduleRepository
                .findByIdAndOrganizationId(
                        scheduleId, orgId)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Schedule not found"));
    }

    private void saveRecord(
            UUID orgId, Item item,
            ServiceType serviceType,
            ServiceSchedule schedule,
            String summary, String serviceDate,
            String techName) {
        LocalDate date = LocalDate.parse(serviceDate);
        ServiceRecord record = new ServiceRecord();
        record.setOrganizationId(orgId);
        record.setItem(item);
        record.setServiceType(serviceType);
        record.setServiceSchedule(schedule);
        record.setServiceDate(date);
        record.setSummary(summary);
        if (techName != null && !techName.isBlank()) {
            record.setDescription(
                    "Technician: " + techName.trim());
        }
        recordRepository.save(record);
        log.info("Saved service record for item {}",
                item.getId());
    }

    private ServiceType findServiceType(
            UUID serviceTypeId, UUID orgId) {
        return serviceTypeRepository
                .findByIdAndOrganizationId(
                        serviceTypeId, orgId)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Service type not found"));
    }

    private Vendor resolveVendor(
            UUID orgId, String vendorId,
            String newVendorName,
            String newVendorPhone) {
        if ("__new__".equals(vendorId)) {
            return createVendor(orgId,
                    newVendorName, newVendorPhone);
        }
        if (vendorId != null && !vendorId.isBlank()) {
            return vendorRepository
                    .findByIdAndOrganizationId(
                            UUID.fromString(vendorId),
                            orgId)
                    .orElseThrow(() ->
                            new IllegalArgumentException(
                                    "Vendor not found"));
        }
        return null;
    }

    private Vendor createVendor(
            UUID orgId, String name, String phone) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException(
                    "Vendor name is required");
        }
        Vendor vendor = new Vendor();
        vendor.setOrganizationId(orgId);
        vendor.setName(name.trim());
        if (phone != null && !phone.isBlank()) {
            vendor.setPhone(phone.trim());
        }
        return vendorRepository.save(vendor);
    }

    private void createSchedule(
            UUID orgId, Item item,
            ServiceType serviceType, Vendor vendor,
            String nextDueDate,
            int frequencyInterval,
            FrequencyUnit frequencyUnit) {
        LocalDate due = LocalDate.parse(nextDueDate);
        ServiceSchedule newSched =
                new ServiceSchedule();
        newSched.setOrganizationId(orgId);
        newSched.setItem(item);
        newSched.setServiceType(serviceType);
        newSched.setPreferredVendor(vendor);
        newSched.setFrequencyUnit(frequencyUnit);
        newSched.setFrequencyInterval(frequencyInterval);
        newSched.setFirstDueDate(due);
        newSched.setNextDueDate(due);
        scheduleRepository.save(newSched);
        log.info("Created schedule for item {}",
                item.getId());
    }

    private void setOrgMdc(AppUser user) {
        if (user.hasOrganization()) {
            MDC.put(MDC_ORG_ID,
                    user.getOrganization().getId()
                            .toString());
        }
    }
}
