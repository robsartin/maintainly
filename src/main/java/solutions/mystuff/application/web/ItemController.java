package solutions.mystuff.application.web;

import java.security.Principal;
import java.time.LocalDate;
import java.util.Collections;
import java.util.UUID;

import solutions.mystuff.domain.model.AppUser;
import solutions.mystuff.domain.model.FrequencyUnit;
import solutions.mystuff.domain.model.Item;
import solutions.mystuff.domain.model.PageResult;
import solutions.mystuff.domain.model.ServiceSchedule;
import solutions.mystuff.domain.model.Vendor;
import solutions.mystuff.domain.port.out.ItemRepository;
import solutions.mystuff.domain.port.out
        .ServiceRecordRepository;
import solutions.mystuff.domain.port.out
        .ServiceScheduleRepository;
import solutions.mystuff.domain.port.out
        .VendorRepository;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class ItemController {

    private static final Logger log =
            LoggerFactory.getLogger(ItemController.class);

    private final ItemRepository itemRepository;
    private final ServiceScheduleRepository scheduleRepo;
    private final ServiceRecordRepository recordRepo;
    private final VendorRepository vendorRepository;
    private final ControllerHelper helper;

    public ItemController(
            ItemRepository itemRepository,
            ServiceScheduleRepository scheduleRepo,
            ServiceRecordRepository recordRepo,
            VendorRepository vendorRepository,
            ControllerHelper helper) {
        this.itemRepository = itemRepository;
        this.scheduleRepo = scheduleRepo;
        this.recordRepo = recordRepo;
        this.vendorRepository = vendorRepository;
        this.helper = helper;
    }

    @GetMapping("/")
    public String home() {
        return "redirect:/schedules";
    }

    @GetMapping("/items")
    public String items(
            @RequestParam(required = false) String q,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            Principal principal, Model model,
            HttpServletResponse response) {
        AppUser user = helper.resolveUser(principal);
        if (!user.hasOrganization()) {
            return handleNoOrg(user, model);
        }
        helper.setOrgMdc(user);
        try {
            UUID orgId = user.getOrganization().getId();
            loadItems(q, orgId, page, size,
                    model, response);
            helper.addUserAttrs(user, model);
            return "items";
        } finally {
            helper.clearOrgMdc();
        }
    }

    @GetMapping("/items/detail")
    public String itemDetail(
            @RequestParam UUID itemId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            Principal principal, Model model,
            HttpServletResponse response) {
        AppUser user = helper.resolveUser(principal);
        if (!user.hasOrganization()) {
            return handleNoOrg(user, model);
        }
        helper.setOrgMdc(user);
        try {
            UUID orgId = user.getOrganization().getId();
            loadItems(null, orgId, page, size,
                    model, response);
            helper.addUserAttrs(user, model);
            model.addAttribute("selectedItemId",
                    itemId);
            model.addAttribute("itemRecords",
                    recordRepo
                            .findByItemIdAndOrganizationId(
                                    itemId, orgId));
            model.addAttribute("itemSchedules",
                    scheduleRepo
                            .findByItemIdAndOrganizationId(
                                    itemId, orgId));
            return "items";
        } finally {
            helper.clearOrgMdc();
        }
    }

    @PostMapping("/items/add")
    public String addItem(
            @RequestParam String name,
            @RequestParam(required = false) String location,
            @RequestParam(required = false)
                    String manufacturer,
            @RequestParam(required = false)
                    String modelName,
            @RequestParam(required = false)
                    String serialNumber,
            Principal principal) {
        AppUser user = helper.resolveUser(principal);
        helper.setOrgMdc(user);
        try {
            UUID orgId = user.getOrganization().getId();
            Item item = buildItem(orgId, name,
                    location, manufacturer,
                    modelName, serialNumber);
            itemRepository.save(item);
            log.info("Created item {}", item.getName());
            return "redirect:/items";
        } finally {
            helper.clearOrgMdc();
        }
    }

    @PostMapping("/items/log")
    public String logItemService(
            @RequestParam UUID itemId,
            @RequestParam String summary,
            @RequestParam String serviceDate,
            @RequestParam(required = false)
                    String vendorId,
            @RequestParam(required = false)
                    String newVendorName,
            @RequestParam(required = false)
                    String newVendorPhone,
            @RequestParam(required = false)
                    String techName,
            Principal principal) {
        AppUser user = helper.resolveUser(principal);
        helper.setOrgMdc(user);
        try {
            UUID orgId = user.getOrganization().getId();
            Item item = findItem(itemId, orgId);
            Vendor vendor = helper.resolveVendor(orgId,
                    vendorId, newVendorName,
                    newVendorPhone);
            helper.saveRecord(orgId, item, null, null,
                    vendor, summary, serviceDate,
                    techName);
            return "redirect:/items";
        } finally {
            helper.clearOrgMdc();
        }
    }

    @PostMapping("/items/schedule")
    public String scheduleItemService(
            @RequestParam UUID itemId,
            @RequestParam String serviceType,
            @RequestParam String nextDueDate,
            @RequestParam int frequencyInterval,
            @RequestParam FrequencyUnit frequencyUnit,
            @RequestParam(required = false)
                    String vendorId,
            @RequestParam(required = false)
                    String newVendorName,
            @RequestParam(required = false)
                    String newVendorPhone,
            Principal principal) {
        AppUser user = helper.resolveUser(principal);
        helper.setOrgMdc(user);
        try {
            UUID orgId = user.getOrganization().getId();
            Item item = findItem(itemId, orgId);
            Vendor vendor = helper.resolveVendor(orgId,
                    vendorId, newVendorName,
                    newVendorPhone);
            helper.createSchedule(orgId, item,
                    serviceType.trim(), vendor,
                    nextDueDate, frequencyInterval,
                    frequencyUnit);
            return "redirect:/items";
        } finally {
            helper.clearOrgMdc();
        }
    }

    @PostMapping("/items/complete")
    public String completeSchedule(
            @RequestParam UUID scheduleId,
            @RequestParam String summary,
            @RequestParam String serviceDate,
            @RequestParam(required = false)
                    String vendorId,
            @RequestParam(required = false)
                    String newVendorName,
            @RequestParam(required = false)
                    String newVendorPhone,
            @RequestParam(required = false)
                    String techName,
            Principal principal) {
        AppUser user = helper.resolveUser(principal);
        helper.setOrgMdc(user);
        try {
            UUID orgId = user.getOrganization().getId();
            ServiceSchedule sched =
                    findSchedule(scheduleId, orgId);
            LocalDate completed =
                    LocalDate.parse(serviceDate);
            Vendor vendor = helper.resolveVendor(orgId,
                    vendorId, newVendorName,
                    newVendorPhone);
            helper.saveRecord(orgId, sched.getItem(),
                    sched.getServiceType(), sched,
                    vendor, summary, serviceDate,
                    techName);
            sched.advanceNextDueDate(completed);
            scheduleRepo.save(sched);
            log.info("Completed schedule {}",
                    scheduleId);
            return "redirect:/items/detail?itemId="
                    + sched.getItem().getId();
        } finally {
            helper.clearOrgMdc();
        }
    }

    @PostMapping("/items/skip")
    public String skipSchedule(
            @RequestParam UUID scheduleId,
            Principal principal) {
        AppUser user = helper.resolveUser(principal);
        helper.setOrgMdc(user);
        try {
            UUID orgId = user.getOrganization().getId();
            ServiceSchedule sched =
                    findSchedule(scheduleId, orgId);
            LocalDate current = sched.getNextDueDate();
            if (current == null) {
                current = LocalDate.now();
            }
            sched.advanceNextDueDate(current);
            sched.setLastCompletedDate(null);
            scheduleRepo.save(sched);
            log.info("Skipped schedule {}",
                    scheduleId);
            return "redirect:/items/detail?itemId="
                    + sched.getItem().getId();
        } finally {
            helper.clearOrgMdc();
        }
    }

    private String handleNoOrg(
            AppUser user, Model model) {
        log.warn("User {} has no organization",
                user.getUsername());
        model.addAttribute("noOrganization", true);
        model.addAttribute("items",
                Collections.emptyList());
        return "items";
    }

    private void loadItems(
            String q, UUID orgId, int page, int size,
            Model model, HttpServletResponse response) {
        int safeSize = helper.clampSize(size);
        int safePage = Math.max(0, page);
        PageResult<Item> result;
        if (q != null && !q.isBlank()) {
            log.info("Searching items query={}", q);
            result = itemRepository
                    .searchByOrganizationId(
                            orgId, q, safePage, safeSize);
            model.addAttribute("q", q);
        } else {
            log.info("Listing items page={}", safePage);
            result = itemRepository
                    .findByOrganizationId(
                            orgId, safePage, safeSize);
        }
        model.addAttribute("items", result.content());
        model.addAttribute("itemPage", result);
        LinkHeaderBuilder.addLinkHeader(
                response, "/items", result, q);
        model.addAttribute("vendors",
                vendorRepository
                        .findByOrganizationId(orgId));
        model.addAttribute("frequencyUnits",
                FrequencyUnit.values());
    }

    private Item buildItem(
            UUID orgId, String name, String location,
            String manufacturer, String modelName,
            String serialNumber) {
        ControllerHelper.requireNotBlank(
                name, "Item name");
        ControllerHelper.requireMaxLength(
                name, "Item name", 200);
        ControllerHelper.requireMaxLength(
                location, "Location", 200);
        ControllerHelper.requireMaxLength(
                manufacturer, "Manufacturer", 200);
        ControllerHelper.requireMaxLength(
                modelName, "Model name", 200);
        ControllerHelper.requireMaxLength(
                serialNumber, "Serial number", 200);
        Item item = new Item();
        item.setOrganizationId(orgId);
        item.setName(name.trim());
        setIfPresent(item::setLocation, location);
        setIfPresent(item::setManufacturer,
                manufacturer);
        setIfPresent(item::setModelName, modelName);
        setIfPresent(item::setSerialNumber,
                serialNumber);
        return item;
    }

    private void setIfPresent(
            java.util.function.Consumer<String> setter,
            String value) {
        if (value != null && !value.isBlank()) {
            setter.accept(value.trim());
        }
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
        return scheduleRepo
                .findByIdAndOrganizationId(
                        scheduleId, orgId)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Schedule not found"));
    }
}
