package solutions.mystuff.application.web;

import java.security.Principal;
import java.time.LocalDate;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;

import solutions.mystuff.domain.model.AppUser;
import solutions.mystuff.domain.model.FrequencyUnit;
import solutions.mystuff.domain.model.Item;
import solutions.mystuff.domain.model.LogSanitizer;
import solutions.mystuff.domain.model.PageResult;
import solutions.mystuff.domain.model.ServiceSchedule;
import solutions.mystuff.domain.model.Vendor;
import solutions.mystuff.domain.port.in.ItemManagement;
import solutions.mystuff.domain.port.in.ItemQuery;
import solutions.mystuff.domain.port.in.ScheduleLifecycle;
import solutions.mystuff.domain.port.in.RecordCreation;
import solutions.mystuff.domain.port.in.VendorManagement;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation
        .RequestParam;

/**
 * Handles item CRUD and service operations at /items endpoints.
 *
 * <div class="mermaid">
 * sequenceDiagram
 *     Browser->>ItemController: GET/POST /items/**
 *     ItemController->>ControllerHelper: resolveUser(principal)
 *     ItemController->>ItemManagement: createItem()
 *     ItemController->>ItemQuery: findByOrganization()
 *     ItemController->>ScheduleLifecycle: createSchedule/complete/skip
 *     ItemController->>RecordCreation: createRecord()
 *     ItemController-->>Browser: Thymeleaf view or redirect
 * </div>
 *
 * @see ControllerHelper
 * @see InputValidator
 */
@Controller
public class ItemController {

    private static final Logger log =
            LoggerFactory.getLogger(ItemController.class);

    private final ControllerHelper helper;
    private final ItemManagement itemService;
    private final ItemQuery itemQuery;
    private final VendorManagement vendorService;
    private final ScheduleLifecycle scheduleService;
    private final RecordCreation recordService;

    public ItemController(
            ControllerHelper helper,
            ItemManagement itemService,
            ItemQuery itemQuery,
            VendorManagement vendorService,
            ScheduleLifecycle scheduleService,
            RecordCreation recordService) {
        this.helper = helper;
        this.itemService = itemService;
        this.itemQuery = itemQuery;
        this.vendorService = vendorService;
        this.scheduleService = scheduleService;
        this.recordService = recordService;
    }

    /** Redirects root to the schedules page. */
    @GetMapping("/")
    public String home() {
        return "redirect:/schedules";
    }

    /** Lists items with optional search and pagination. */
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

    /** Shows item detail with records and schedules. */
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
            addDetailAttrs(model, itemId, orgId);
            return "items";
        } finally {
            helper.clearOrgMdc();
        }
    }

    /** Creates a new item for the user's organization. */
    @PostMapping("/items/add")
    public String addItem(
            @RequestParam String name,
            @RequestParam(required = false)
                    String location,
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
            itemService.createItem(orgId, name,
                    location, manufacturer,
                    modelName, serialNumber);
            return "redirect:/items";
        } finally {
            helper.clearOrgMdc();
        }
    }

    /** Logs a service record for an item, advancing schedule if not one-off. */
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
            @RequestParam(required = false,
                    defaultValue = "false")
                    boolean oneOff,
            Principal principal) {
        AppUser user = helper.resolveUser(principal);
        helper.setOrgMdc(user);
        try {
            UUID orgId = user.getOrganization().getId();
            Item item = findItem(itemId, orgId);
            Vendor vendor = vendorService.resolveVendor(
                    orgId, vendorId, newVendorName,
                    newVendorPhone);
            LocalDate date = InputValidator.parseDate(
                    serviceDate, "Service date");
            if (oneOff) {
                recordService.createRecord(orgId, item,
                        null, null, vendor, summary,
                        date, techName);
            } else {
                completeNextSchedule(orgId, item,
                        vendor, summary, date, techName);
            }
            return "redirect:/items";
        } finally {
            helper.clearOrgMdc();
        }
    }

    private void completeNextSchedule(
            UUID orgId, Item item, Vendor vendor,
            String summary, LocalDate date,
            String techName) {
        List<ServiceSchedule> schedules =
                itemQuery.findSchedulesByItem(
                        item.getId(), orgId);
        ServiceSchedule next = schedules.stream()
                .filter(ServiceSchedule::isActive)
                .min(Comparator.comparing(
                        s -> s.getNextDueDate() != null
                                ? s.getNextDueDate()
                                : LocalDate.MAX))
                .orElse(null);
        if (next != null) {
            scheduleService.completeSchedule(
                    next.getId(), orgId, vendor,
                    summary, date, techName);
        } else {
            recordService.createRecord(orgId, item,
                    null, null, vendor, summary,
                    date, techName);
        }
    }

    /** Creates a recurring service schedule for an item. */
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
            Vendor vendor = vendorService.resolveVendor(
                    orgId, vendorId, newVendorName,
                    newVendorPhone);
            LocalDate due = InputValidator.parseDate(
                    nextDueDate, "Next due date");
            scheduleService.createSchedule(orgId,
                    itemId, serviceType, vendor, due,
                    frequencyInterval, frequencyUnit);
            return "redirect:/items";
        } finally {
            helper.clearOrgMdc();
        }
    }

    /** Marks a scheduled service as completed. */
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
            Vendor vendor = vendorService.resolveVendor(
                    orgId, vendorId, newVendorName,
                    newVendorPhone);
            LocalDate date = InputValidator.parseDate(
                    serviceDate, "Service date");
            ServiceSchedule sched =
                    scheduleService.completeSchedule(
                            scheduleId, orgId, vendor,
                            summary, date, techName);
            return "redirect:/items/detail?itemId="
                    + sched.getItem().getId();
        } finally {
            helper.clearOrgMdc();
        }
    }

    /** Skips the current occurrence and advances the due date. */
    @PostMapping("/items/skip")
    public String skipSchedule(
            @RequestParam UUID scheduleId,
            Principal principal) {
        AppUser user = helper.resolveUser(principal);
        helper.setOrgMdc(user);
        try {
            UUID orgId = user.getOrganization().getId();
            ServiceSchedule sched =
                    scheduleService.skipSchedule(
                            scheduleId, orgId);
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

    private void addDetailAttrs(
            Model model, UUID itemId, UUID orgId) {
        model.addAttribute("selectedItemId", itemId);
        model.addAttribute("itemRecords",
                itemQuery.findRecordsByItem(
                        itemId, orgId));
        model.addAttribute("itemSchedules",
                itemQuery.findSchedulesByItem(
                        itemId, orgId));
    }

    private void loadItems(
            String q, UUID orgId, int page, int size,
            Model model, HttpServletResponse response) {
        int safeSize = helper.clampSize(size);
        int safePage = Math.max(0, page);
        PageResult<Item> result;
        if (q != null && !q.isBlank()) {
            log.info("Searching items query={}",
                    LogSanitizer.sanitize(q));
            result = itemQuery.searchByOrganization(
                    orgId, q, safePage, safeSize);
            model.addAttribute("q", q);
        } else {
            log.info("Listing items page={}", safePage);
            result = itemQuery.findByOrganization(
                    orgId, safePage, safeSize);
        }
        model.addAttribute("items", result.content());
        model.addAttribute("itemPage", result);
        LinkHeaderBuilder.addLinkHeader(
                response, "/items", result, q);
        model.addAttribute("vendors",
                itemQuery.findVendorsByOrganization(
                        orgId));
        model.addAttribute("frequencyUnits",
                FrequencyUnit.values());
    }

    private Item findItem(UUID itemId, UUID orgId) {
        return itemQuery
                .findByIdAndOrganization(itemId, orgId)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Item not found"));
    }
}
