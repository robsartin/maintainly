package solutions.mystuff.application.web;

import java.security.Principal;
import java.time.LocalDate;
import java.util.UUID;

import solutions.mystuff.domain.model.AppUser;
import solutions.mystuff.domain.model.FrequencyUnit;
import solutions.mystuff.domain.model.Item;
import solutions.mystuff.domain.model.LogSanitizer;
import solutions.mystuff.domain.model.PageResult;
import solutions.mystuff.domain.model.Vendor;
import solutions.mystuff.domain.port.in.ItemManagement;
import solutions.mystuff.domain.port.in.ItemQuery;
import solutions.mystuff.domain.port.in.ScheduleLifecycle;
import solutions.mystuff.domain.port.in.RecordCreation;
import solutions.mystuff.domain.port.in.VendorQuery;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation
        .RequestParam;

/**
 * Handles item CRUD and service operations at /items endpoints.
 *
 * @see ControllerHelper
 * @see InputValidator
 */
@Controller
@Tag(name = "Items",
        description = "Item CRUD and service operations")
public class ItemController {

    private static final Logger log =
            LoggerFactory.getLogger(ItemController.class);

    private final ControllerHelper helper;
    private final ItemManagement itemService;
    private final ItemQuery itemQuery;
    private final VendorQuery vendorQuery;
    private final ScheduleLifecycle scheduleService;
    private final RecordCreation recordService;

    public ItemController(
            ControllerHelper helper,
            ItemManagement itemService,
            ItemQuery itemQuery,
            VendorQuery vendorQuery,
            ScheduleLifecycle scheduleService,
            RecordCreation recordService) {
        this.helper = helper;
        this.itemService = itemService;
        this.itemQuery = itemQuery;
        this.vendorQuery = vendorQuery;
        this.scheduleService = scheduleService;
        this.recordService = recordService;
    }

    @Operation(summary = "Redirect to schedules")
    @GetMapping("/")
    public String home() {
        return "redirect:/schedules";
    }

    @Operation(summary = "List items",
            description = "Lists items with optional"
                    + " search and pagination")
    @GetMapping("/items")
    public String items(
            @RequestParam(required = false) String q,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            Principal principal, Model model,
            HttpServletResponse response) {
        AppUser user = helper.resolveUser(principal);
        if (!user.hasOrganization()) {
            return helper.handleNoOrg(user, model,
                    "items");
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

    @Operation(summary = "Item detail",
            description = "Shows item with service"
                    + " history and schedules")
    @GetMapping("/items/{id}")
    public String itemDetail(
            @PathVariable("id") UUID itemId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            Principal principal, Model model,
            HttpServletResponse response) {
        AppUser user = helper.resolveUser(principal);
        if (!user.hasOrganization()) {
            return helper.handleNoOrg(user, model,
                    "items");
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

    @Operation(summary = "Create item")
    @PostMapping("/items")
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

    @Operation(summary = "Log service record",
            description = "Logs a service visit,"
                    + " advancing schedule unless one-off")
    @PostMapping("/items/{id}/service-records")
    public String logItemService(
            @PathVariable("id") UUID itemId,
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
            Vendor vendor = helper.resolveVendor(
                    orgId, vendorId, newVendorName,
                    newVendorPhone);
            LocalDate date = InputValidator.parseDate(
                    serviceDate, "Service date");
            if (oneOff) {
                recordService.createRecord(orgId, item,
                        null, null, vendor, summary,
                        date, techName);
            } else {
                scheduleService.completeNextForItem(
                        orgId, itemId, vendor,
                        summary, date, techName);
            }
            return "redirect:/items";
        } finally {
            helper.clearOrgMdc();
        }
    }

    @Operation(summary = "Create schedule",
            description = "Creates a recurring service"
                    + " schedule for an item")
    @PostMapping("/items/{id}/schedules")
    public String scheduleItemService(
            @PathVariable("id") UUID itemId,
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
            @RequestParam(required = false)
                    String redirectTo,
            Principal principal) {
        AppUser user = helper.resolveUser(principal);
        helper.setOrgMdc(user);
        try {
            UUID orgId = user.getOrganization().getId();
            Vendor vendor = helper.resolveVendor(
                    orgId, vendorId, newVendorName,
                    newVendorPhone);
            LocalDate due = InputValidator.parseDate(
                    nextDueDate, "Next due date");
            scheduleService.createSchedule(orgId,
                    itemId, serviceType, vendor, due,
                    frequencyInterval, frequencyUnit);
            if ("schedules".equals(redirectTo)) {
                return "redirect:/schedules";
            }
            return "redirect:/items";
        } finally {
            helper.clearOrgMdc();
        }
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
                vendorQuery.findAllVendors(orgId));
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
