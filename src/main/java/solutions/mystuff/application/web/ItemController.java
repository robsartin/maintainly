package solutions.mystuff.application.web;

import java.math.BigDecimal;
import java.security.Principal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import solutions.mystuff.domain.model.AppUser;
import solutions.mystuff.domain.model.AuditAction;
import solutions.mystuff.domain.model.FrequencyUnit;
import solutions.mystuff.domain.model.Item;
import solutions.mystuff.domain.model.ItemSpec;
import solutions.mystuff.domain.model.NotFoundException;
import solutions.mystuff.domain.model.PageRequest;
import solutions.mystuff.domain.model.PageResult;
import solutions.mystuff.domain.model.Validation;
import solutions.mystuff.domain.model.ServiceCompletion;
import solutions.mystuff.domain.model.ServiceSchedule;
import solutions.mystuff.domain.model.Vendor;
import solutions.mystuff.domain.port.in.AuditLog;
import solutions.mystuff.domain.port.in.ItemManagement;
import solutions.mystuff.domain.port.in.ItemQuery;
import solutions.mystuff.domain.port.in.RecordCreation;
import solutions.mystuff.domain.port.in.RecordManagement;
import solutions.mystuff.domain.port.in.ScheduleLifecycle;
import solutions.mystuff.domain.port.in.VendorQuery;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.security.access.prepost
        .PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation
        .RequestParam;
import org.springframework.web.servlet.mvc
        .support.RedirectAttributes;

/**
 * Handles item CRUD and service operations at /items endpoints.
 *
 * <div class="mermaid">
 * sequenceDiagram
 *     Browser->>ItemController: GET/POST/PUT/DELETE /items
 *     ItemController->>ItemManagement: create/update/delete/bulk
 *     ItemController->>ItemQuery: find/search/filter
 *     ItemController-->>Browser: HTML (Thymeleaf)
 * </div>
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
    private final RecordManagement recordMgmt;
    private final AuditLog auditLog;

    public ItemController(
            ControllerHelper helper,
            ItemManagement itemService,
            ItemQuery itemQuery,
            VendorQuery vendorQuery,
            ScheduleLifecycle scheduleService,
            RecordCreation recordService,
            RecordManagement recordMgmt,
            AuditLog auditLog) {
        this.helper = helper;
        this.itemService = itemService;
        this.itemQuery = itemQuery;
        this.vendorQuery = vendorQuery;
        this.scheduleService = scheduleService;
        this.recordService = recordService;
        this.recordMgmt = recordMgmt;
        this.auditLog = auditLog;
    }

    @Operation(summary = "List items",
            description = "Returns a paginated, sortable"
                    + " list of items with optional"
                    + " full-text search and category"
                    + " filter."
                    + " Model attributes:"
                    + " items (List<Item>),"
                    + " itemPage (PageResult),"
                    + " categories (List<String>),"
                    + " vendors (List<Vendor>),"
                    + " frequencyUnits (FrequencyUnit[])."
                    + " Includes Link header for"
                    + " pagination.",

            responses = {
                    @ApiResponse(responseCode = "200",
                            description = "HTML page with"
                                    + " item table"),
                    @ApiResponse(responseCode = "200",
                            description = "No-org warning"
                                    + " if user lacks an"
                                    + " organization")})
    @GetMapping("/items")
    public String items(
            @Parameter(description = "Search query")
            @RequestParam(required = false) String q,
            @Parameter(description = "Category filter")
            @RequestParam(required = false)
                    String category,
            @Parameter(description = "Zero-based page"
                    + " index")
            @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Page size"
                    + " (max 100)")
            @RequestParam(defaultValue = "10") int size,
            @Parameter(description = "Sort field")
            @RequestParam(defaultValue = "name")
                    String sort,
            @Parameter(description = "Sort direction"
                    + " (asc or desc)")
            @RequestParam(defaultValue = "asc")
                    String dir,
            Principal principal, Model model,
            HttpServletResponse response) {
        AppUser user = helper.resolveUser(principal);
        if (!user.hasOrganization()) {
            return helper.handleNoOrg(user, model,
                    "items");
        }
        helper.setOrgMdc(user);
        UUID orgId = user.getOrganization().getId();
        PageRequest pageReq = new PageRequest(
                page, size, sort, dir);
        loadItems(q, category, orgId, pageReq,
                model, response);
        helper.addUserAttrs(user, model);
        return "items";
    }

    @Operation(summary = "Item detail",
            description = "Shows item detail with"
                    + " service history and schedules.",
            responses = {
                    @ApiResponse(responseCode = "200",
                            description = "HTML page"),
                    @ApiResponse(responseCode = "404",
                            description = "Item not"
                                    + " found")})
    @GetMapping("/items/{id}")
    public String itemDetail(
            @Parameter(description = "Item UUID")
            @PathVariable("id") UUID itemId,
            @Parameter(description = "Page index")
            @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Page size")
            @RequestParam(defaultValue = "10") int size,
            Principal principal, Model model,
            HttpServletResponse response) {
        AppUser user = helper.resolveUser(principal);
        if (!user.hasOrganization()) {
            return helper.handleNoOrg(user, model,
                    "items");
        }
        helper.setOrgMdc(user);
        UUID orgId = user.getOrganization().getId();
        loadItems(null, null, orgId,
                new PageRequest(page, size),
                model, response);
        helper.addUserAttrs(user, model);
        addDetailAttrs(model, itemId, orgId);
        return "items";
    }

    @Operation(summary = "Create item",
            description = "Creates a new item.",
            responses = {
                    @ApiResponse(responseCode = "302",
                            description = "Redirect to"
                                    + " /items"),
                    @ApiResponse(responseCode = "400",
                            description = "Validation"
                                    + " error")})
    @PostMapping("/items")
    @PreAuthorize("@roleCheck.canWrite(#principal)")
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
            @RequestParam(required = false)
                    String modelNumber,
            @RequestParam(required = false)
                    Integer modelYear,
            @RequestParam(required = false)
                    String category,
            @RequestParam(required = false)
                    String purchaseDate,
            @RequestParam(required = false) String notes,
            Principal principal,
            RedirectAttributes redirectAttrs) {
        AppUser user = helper.resolveUser(principal);
        helper.setOrgMdc(user);
        UUID orgId = user.getOrganization().getId();
        LocalDate pd = InputValidator.parseDateOrNull(
                purchaseDate, "Purchase date");
        ItemSpec spec = new ItemSpec(name, location,
                manufacturer, modelName, serialNumber,
                modelNumber, modelYear, category,
                pd, notes);
        Item created = itemService.createItem(orgId, spec);
        auditLog.log(orgId, user.getUsername(),
                "Item", created.getId(),
                created.getName(),
                AuditAction.CREATE, null);
        redirectAttrs.addFlashAttribute(
                "success", "Item created");
        return "redirect:/items";
    }

    @Operation(summary = "Update item",
            description = "Updates an existing item.",
            responses = {
                    @ApiResponse(responseCode = "302",
                            description = "Redirect to"
                                    + " /items"),
                    @ApiResponse(responseCode = "400",
                            description = "Validation"
                                    + " error"),
                    @ApiResponse(responseCode = "404",
                            description = "Item not"
                                    + " found")})
    @PutMapping("/items/{id}")
    @PreAuthorize("@roleCheck.canWrite(#principal)")
    public String editItem(
            @PathVariable("id") UUID itemId,
            @RequestParam String name,
            @RequestParam(required = false)
                    String location,
            @RequestParam(required = false)
                    String manufacturer,
            @RequestParam(required = false)
                    String modelName,
            @RequestParam(required = false)
                    String modelNumber,
            @RequestParam(required = false)
                    Integer modelYear,
            @RequestParam(required = false)
                    String serialNumber,
            @RequestParam(required = false)
                    String purchaseDate,
            @RequestParam(required = false)
                    String category,
            @RequestParam(required = false) String notes,
            Principal principal,
            RedirectAttributes redirectAttrs) {
        AppUser user = helper.resolveUser(principal);
        helper.setOrgMdc(user);
        UUID orgId = user.getOrganization().getId();
        LocalDate pd = InputValidator.parseDateOrNull(
                purchaseDate, "Purchase date");
        ItemSpec spec = new ItemSpec(name, location,
                manufacturer, modelName, serialNumber,
                modelNumber, modelYear, category,
                pd, notes);
        Item updated = itemService.updateItem(
                orgId, itemId, spec);
        auditLog.log(orgId, user.getUsername(),
                "Item", updated.getId(),
                updated.getName(),
                AuditAction.UPDATE, null);
        redirectAttrs.addFlashAttribute(
                "success", "Item updated");
        return "redirect:/items";
    }

    @Operation(summary = "Delete item",
            description = "Permanently deletes an item"
                    + " and all associated schedules"
                    + " and records via cascade.",
            responses = {
                    @ApiResponse(responseCode = "302",
                            description = "Redirect to"
                                    + " /items"),
                    @ApiResponse(responseCode = "404",
                            description = "Item not"
                                    + " found")})
    @DeleteMapping("/items/{id}")
    @PreAuthorize("@roleCheck.canDelete(#principal)")
    public String deleteItem(
            @Parameter(description = "Item UUID")
            @PathVariable("id") UUID itemId,
            Principal principal,
            RedirectAttributes redirectAttrs) {
        AppUser user = helper.resolveUser(principal);
        helper.setOrgMdc(user);
        UUID orgId = user.getOrganization().getId();
        String itemName = itemQuery
                .findByIdAndOrganization(itemId, orgId)
                .map(Item::getName).orElse("Unknown");
        itemService.deleteItem(orgId, itemId);
        auditLog.log(orgId, user.getUsername(),
                "Item", itemId, itemName,
                AuditAction.DELETE, null);
        redirectAttrs.addFlashAttribute(
                "success", "Item deleted");
        return "redirect:/items";
    }

    @Operation(summary = "Bulk delete items",
            description = "Deletes multiple items by ID.",
            responses = {
                    @ApiResponse(responseCode = "302",
                            description = "Redirect to"
                                    + " /items")})
    @PostMapping("/items/bulk-delete")
    public String bulkDelete(
            @RequestParam("itemIds")
                    List<UUID> itemIds,
            Principal principal,
            RedirectAttributes redirectAttrs) {
        AppUser user = helper.resolveUser(principal);
        helper.setOrgMdc(user);
        itemService.bulkDelete(
                user.getOrganization().getId(),
                itemIds);
        redirectAttrs.addFlashAttribute("success",
                itemIds.size() + " item(s) deleted");
        return "redirect:/items";
    }

    @Operation(summary = "Bulk update category",
            description = "Updates category for"
                    + " multiple items.",
            responses = {
                    @ApiResponse(responseCode = "302",
                            description = "Redirect to"
                                    + " /items")})
    @PostMapping("/items/bulk-category")
    public String bulkUpdateCategory(
            @RequestParam("itemIds")
                    List<UUID> itemIds,
            @RequestParam("category")
                    String category,
            Principal principal,
            RedirectAttributes redirectAttrs) {
        AppUser user = helper.resolveUser(principal);
        helper.setOrgMdc(user);
        itemService.bulkUpdateCategory(
                user.getOrganization().getId(),
                itemIds, category);
        redirectAttrs.addFlashAttribute("success",
                itemIds.size()
                        + " item(s) updated");
        return "redirect:/items";
    }

    @Operation(summary = "Log service record",
            description = "Logs a service visit for an"
                    + " item.",
            responses = {
                    @ApiResponse(responseCode = "302",
                            description = "Redirect to"
                                    + " /items"),
                    @ApiResponse(responseCode = "400",
                            description = "Validation"
                                    + " error"),
                    @ApiResponse(responseCode = "404",
                            description = "Item not"
                                    + " found")})
    @PostMapping("/items/{id}/service-records")
    @PreAuthorize("@roleCheck.canWrite(#principal)")
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
            @RequestParam(required = false)
                    BigDecimal cost,
            Principal principal,
            RedirectAttributes redirectAttrs) {
        AppUser user = helper.resolveUser(principal);
        helper.setOrgMdc(user);
        UUID orgId = user.getOrganization().getId();
        Item item = findItem(itemId, orgId);
        Vendor vendor = helper.resolveVendor(
                orgId, vendorId, newVendorName,
                newVendorPhone);
        LocalDate date = InputValidator.parseDate(
                serviceDate, "Service date");
        ServiceCompletion completion =
                new ServiceCompletion(vendor, summary,
                        date, techName, cost);
        if (oneOff) {
            recordService.createRecord(
                    orgId, item, null, completion);
        } else {
            scheduleService.completeNextForItem(
                    orgId, itemId, completion);
        }
        auditLog.log(orgId, user.getUsername(),
                "Record", itemId, item.getName(),
                AuditAction.CREATE,
                "Service record logged");
        redirectAttrs.addFlashAttribute(
                "success", "Service record logged");
        return "redirect:/items";
    }

    @Operation(summary = "Update service record",
            description = "Updates an existing service"
                    + " record. Redirects to item"
                    + " detail.",
            responses = {
                    @ApiResponse(responseCode = "302",
                            description = "Redirect to"
                                    + " item detail"),
                    @ApiResponse(responseCode = "400",
                            description = "Validation"
                                    + " error"),
                    @ApiResponse(responseCode = "404",
                            description = "Record not"
                                    + " found")})
    @PutMapping("/items/{itemId}/records/{recordId}")
    @PreAuthorize("@roleCheck.canWrite(#principal)")
    public String editRecord(
            @PathVariable("itemId") UUID itemId,
            @PathVariable("recordId") UUID recordId,
            @RequestParam String summary,
            @RequestParam String serviceDate,
            @RequestParam(required = false)
                    String techName,
            @RequestParam(required = false)
                    BigDecimal cost,
            Principal principal,
            RedirectAttributes redirectAttrs) {
        AppUser user = helper.resolveUser(principal);
        helper.setOrgMdc(user);
        UUID orgId = user.getOrganization().getId();
        LocalDate date = InputValidator.parseDate(
                serviceDate, "Service date");
        recordMgmt.updateRecord(orgId, recordId,
                summary, date, techName, cost);
        auditLog.log(orgId, user.getUsername(),
                "Record", recordId, summary,
                AuditAction.UPDATE, null);
        redirectAttrs.addFlashAttribute(
                "success", "Record updated");
        return "redirect:/items/" + itemId;
    }

    @Operation(summary = "Delete service record",
            description = "Deletes a service record."
                    + " Redirects to item detail.",
            responses = {
                    @ApiResponse(responseCode = "302",
                            description = "Redirect to"
                                    + " item detail"),
                    @ApiResponse(responseCode = "404",
                            description = "Record not"
                                    + " found")})
    @DeleteMapping(
            "/items/{itemId}/records/{recordId}")
    @PreAuthorize("@roleCheck.canDelete(#principal)")
    public String deleteRecord(
            @PathVariable("itemId") UUID itemId,
            @PathVariable("recordId") UUID recordId,
            Principal principal,
            RedirectAttributes redirectAttrs) {
        AppUser user = helper.resolveUser(principal);
        helper.setOrgMdc(user);
        UUID orgId = user.getOrganization().getId();
        recordMgmt.deleteRecord(orgId, recordId);
        auditLog.log(orgId, user.getUsername(),
                "Record", recordId, "Service record",
                AuditAction.DELETE, null);
        redirectAttrs.addFlashAttribute(
                "success", "Record deleted");
        return "redirect:/items/" + itemId;
    }

    @Operation(summary = "Create schedule",
            description = "Creates a recurring service"
                    + " schedule for an item.",
            responses = {
                    @ApiResponse(responseCode = "302",
                            description = "Redirect"),
                    @ApiResponse(responseCode = "400",
                            description = "Validation"
                                    + " error")})
    @PostMapping("/items/{id}/schedules")
    @PreAuthorize("@roleCheck.canWrite(#principal)")
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
            Principal principal,
            RedirectAttributes redirectAttrs) {
        AppUser user = helper.resolveUser(principal);
        helper.setOrgMdc(user);
        UUID orgId = user.getOrganization().getId();
        Vendor vendor = helper.resolveVendor(
                orgId, vendorId, newVendorName,
                newVendorPhone);
        LocalDate due = InputValidator.parseDate(
                nextDueDate, "Next due date");
        ServiceSchedule sched =
                scheduleService.createSchedule(orgId,
                        itemId, serviceType, vendor, due,
                        frequencyInterval, frequencyUnit);
        auditLog.log(orgId, user.getUsername(),
                "Schedule", sched.getId(),
                serviceType, AuditAction.CREATE, null);
        redirectAttrs.addFlashAttribute(
                "success", "Schedule created");
        if ("schedules".equals(redirectTo)) {
            return "redirect:/schedules";
        }
        return "redirect:/items";
    }

    private void addDetailAttrs(
            Model model, UUID itemId, UUID orgId) {
        model.addAttribute("selectedItemId", itemId);
        itemQuery.findByIdAndOrganization(itemId, orgId)
                .ifPresent(item -> model.addAttribute(
                        "selectedItemName", item.getName()));
        model.addAttribute("itemRecords",
                itemQuery.findRecordsByItem(
                        itemId, orgId));
        model.addAttribute("itemSchedules",
                itemQuery.findSchedulesByItem(
                        itemId, orgId));
        model.addAttribute("itemAuditEntries",
                auditLog.findByEntityId(itemId));
    }

    private void loadItems(
            String q, String category,
            UUID orgId, PageRequest pageReq,
            Model model, HttpServletResponse response) {
        int safeSize = helper.clampSize(pageReq.size());
        int safePage = Math.max(0, pageReq.page());
        PageRequest safe = new PageRequest(
                safePage, safeSize,
                pageReq.sort(), pageReq.dir());
        PageResult<Item> result = itemQuery.findItems(
                orgId, q, category, safe);
        String cat = Validation.trimOrNull(category);
        if (q != null && !q.isBlank()) {
            model.addAttribute("q", q);
        }
        if (cat != null) {
            model.addAttribute("selectedCategory", cat);
        }
        model.addAttribute("items", result.content());
        model.addAttribute("itemPage", result);
        model.addAttribute("categories",
                itemQuery.findDistinctCategories(orgId));
        LinkHeaderBuilder.addLinkHeader(
                response, "/items", result, q, cat);
        model.addAttribute("vendors",
                vendorQuery.findAllVendors(orgId));
        model.addAttribute("frequencyUnits",
                FrequencyUnit.values());
    }

    private Item findItem(UUID itemId, UUID orgId) {
        return itemQuery
                .findByIdAndOrganization(itemId, orgId)
                .orElseThrow(() ->
                        new NotFoundException(
                                "Item not found"));
    }

}
