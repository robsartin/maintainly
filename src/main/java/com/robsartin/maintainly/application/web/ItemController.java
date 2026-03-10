package com.robsartin.maintainly.application.web;

import java.security.Principal;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import com.robsartin.maintainly.domain.model.AppUser;
import com.robsartin.maintainly.domain.model.Item;
import com.robsartin.maintainly.domain.model.ServiceRecord;
import com.robsartin.maintainly.domain.model.ServiceSchedule;
import com.robsartin.maintainly.domain.port.in.UserResolver;
import com.robsartin.maintainly.domain.port.out.ItemRepository;
import com.robsartin.maintainly.domain.port.out.ServiceRecordRepository;
import com.robsartin.maintainly.domain.port.out.ServiceScheduleRepository;
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
    private final UserResolver userResolver;

    public ItemController(
            ItemRepository itemRepository,
            ServiceScheduleRepository scheduleRepository,
            ServiceRecordRepository recordRepository,
            UserResolver userResolver) {
        this.itemRepository = itemRepository;
        this.scheduleRepository = scheduleRepository;
        this.recordRepository = recordRepository;
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
            return "home";
        } finally {
            MDC.remove(MDC_ORG_ID);
        }
    }

    @PostMapping("/service/record")
    public String addServiceRecord(
            @RequestParam UUID itemId,
            @RequestParam String summary,
            @RequestParam String serviceDate,
            Principal principal, Model model) {
        AppUser user = userResolver.resolveOrCreate(
                principal.getName());
        setOrgMdc(user);
        try {
            UUID orgId = user.getOrganization().getId();
            Item item = itemRepository
                    .findByIdAndOrganizationId(
                            itemId, orgId)
                    .orElseThrow(() ->
                            new IllegalArgumentException(
                                    "Item not found"));
            LocalDate date =
                    LocalDate.parse(serviceDate);
            ServiceRecord record = new ServiceRecord();
            record.setOrganizationId(orgId);
            record.setItem(item);
            record.setServiceDate(date);
            record.setSummary(summary);
            recordRepository.save(record);
            log.info("Created service record for item {}",
                    itemId);
            return index(null, principal, model);
        } finally {
            MDC.remove(MDC_ORG_ID);
        }
    }

    @PostMapping("/schedule/log")
    public String logService(
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
            createRecord(sched, orgId, summary,
                    serviceDate, techName);
            return index(null, principal, model);
        } finally {
            MDC.remove(MDC_ORG_ID);
        }
    }

    @PostMapping("/schedule/new")
    public String scheduleService(
            @RequestParam UUID scheduleId,
            @RequestParam String nextDueDate,
            Principal principal, Model model) {
        AppUser user = userResolver.resolveOrCreate(
                principal.getName());
        setOrgMdc(user);
        try {
            UUID orgId = user.getOrganization().getId();
            ServiceSchedule source = findSchedule(
                    scheduleId, orgId);
            createScheduleFrom(source, orgId,
                    nextDueDate);
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

    private ServiceSchedule findSchedule(
            UUID scheduleId, UUID orgId) {
        return scheduleRepository
                .findByIdAndOrganizationId(
                        scheduleId, orgId)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Schedule not found"));
    }

    private void createRecord(
            ServiceSchedule sched, UUID orgId,
            String summary, String serviceDate,
            String techName) {
        LocalDate date = LocalDate.parse(serviceDate);
        ServiceRecord record = new ServiceRecord();
        record.setOrganizationId(orgId);
        record.setItem(sched.getItem());
        record.setServiceType(sched.getServiceType());
        record.setServiceSchedule(sched);
        record.setServiceDate(date);
        record.setSummary(summary);
        if (techName != null && !techName.isBlank()) {
            record.setDescription(
                    "Technician: " + techName.trim());
        }
        recordRepository.save(record);
        sched.setLastCompletedDate(date);
        scheduleRepository.save(sched);
        log.info("Logged service for schedule {}",
                sched.getId());
    }

    private void createScheduleFrom(
            ServiceSchedule source, UUID orgId,
            String nextDueDate) {
        LocalDate due = LocalDate.parse(nextDueDate);
        ServiceSchedule newSched =
                new ServiceSchedule();
        newSched.setOrganizationId(orgId);
        newSched.setItem(source.getItem());
        newSched.setServiceType(
                source.getServiceType());
        newSched.setPreferredVendor(
                source.getPreferredVendor());
        newSched.setFrequencyUnit(
                source.getFrequencyUnit());
        newSched.setFrequencyInterval(
                source.getFrequencyInterval());
        newSched.setFirstDueDate(due);
        newSched.setNextDueDate(due);
        scheduleRepository.save(newSched);
        log.info("Created new schedule for item {}",
                source.getItem().getId());
    }

    private void setOrgMdc(AppUser user) {
        if (user.hasOrganization()) {
            MDC.put(MDC_ORG_ID,
                    user.getOrganization().getId()
                            .toString());
        }
    }
}
