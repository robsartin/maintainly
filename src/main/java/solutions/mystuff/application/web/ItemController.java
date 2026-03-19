package solutions.mystuff.application.web;

import java.math.BigDecimal;
import java.security.Principal;
import java.time.LocalDate;
import java.util.UUID;

import solutions.mystuff.domain.model.AppUser;
import solutions.mystuff.domain.model.FrequencyUnit;
import solutions.mystuff.domain.model.Item;
import solutions.mystuff.domain.model.ItemSpec;
import solutions.mystuff.domain.model.LogSanitizer;
import solutions.mystuff.domain.model.NotFoundException;
import solutions.mystuff.domain.model.PageResult;
import solutions.mystuff.domain.model.Validation;
import solutions.mystuff.domain.model.ServiceCompletion;
import solutions.mystuff.domain.model.Vendor;
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
 *     ItemController->>ItemManagement: create/update/delete
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

    public ItemController(
            ControllerHelper helper,
            ItemManagement itemService,
            ItemQuery itemQuery,
            VendorQuery vendorQuery,
            ScheduleLifecycle scheduleService,
            RecordCreation recordService,
            RecordManagement recordMgmt) {
        this.helper = helper;
        this.itemService = itemService;
        this.itemQuery = itemQuery;
        this.vendorQuery = vendorQuery;
        this.scheduleService = scheduleService;
        this.recordService = recordService;
        this.recordMgmt = recordMgmt;
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
        loadItems(q, category, orgId, page, size,
                sort, dir, model, response);
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
        loadItems(null, null, orgId, page, size,
                "name", "asc", model, response);
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
        itemService.createItem(orgId, spec);
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
        itemService.updateItem(orgId, itemId, spec);
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
    public String deleteItem(
            @Parameter(description = "Item UUID")
            @PathVariable("id") UUID itemId,
            Principal principal,
            RedirectAttributes redirectAttrs) {
        AppUser user = helper.resolveUser(principal);
        helper.setOrgMdc(user);
        itemService.deleteItem(
                user.getOrganization().getId(),
                itemId);
        redirectAttrs.addFlashAttribute(
                "success", "Item deleted");
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
    public String deleteRecord(
            @PathVariable("itemId") UUID itemId,
            @PathVariable("recordId") UUID recordId,
            Principal principal,
            RedirectAttributes redirectAttrs) {
        AppUser user = helper.resolveUser(principal);
        helper.setOrgMdc(user);
        UUID orgId = user.getOrganization().getId();
        recordMgmt.deleteRecord(orgId, recordId);
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
        scheduleService.createSchedule(orgId,
                itemId, serviceType, vendor, due,
                frequencyInterval, frequencyUnit);
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
        model.addAttribute("itemRecords",
                itemQuery.findRecordsByItem(
                        itemId, orgId));
        model.addAttribute("itemSchedules",
                itemQuery.findSchedulesByItem(
                        itemId, orgId));
    }

    private void loadItems(
            String q, String category,
            UUID orgId, int page, int size,
            String sort, String dir,
            Model model, HttpServletResponse response) {
        int safeSize = helper.clampSize(size);
        int safePage = Math.max(0, page);
        String cat = normalizeCategory(category);
        PageResult<Item> result = queryItems(
                q, cat, orgId, safePage, safeSize,
                sort, dir);
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

    private PageResult<Item> queryItems(
            String q, String category,
            UUID orgId, int page, int size,
            String sort, String dir) {
        boolean hasQuery = q != null && !q.isBlank();
        boolean hasCat = category != null;
        if (hasQuery && hasCat) {
            log.info("Searching items query={} cat={}",
                    LogSanitizer.sanitize(q),
                    LogSanitizer.sanitize(category));
            return itemQuery
                    .searchByCategoryAndOrganization(
                            orgId, q, category,
                            page, size, sort, dir);
        } else if (hasQuery) {
            log.info("Searching items query={}",
                    LogSanitizer.sanitize(q));
            return itemQuery.searchByOrganization(
                    orgId, q, page, size, sort, dir);
        } else if (hasCat) {
            log.info("Listing items cat={}",
                    LogSanitizer.sanitize(category));
            return itemQuery
                    .findByCategoryAndOrganization(
                            orgId, category, page, size,
                            sort, dir);
        } else {
            log.info("Listing items page={}", page);
            return itemQuery.findByOrganization(
                    orgId, page, size, sort, dir);
        }
    }

    private String normalizeCategory(String category) {
        return Validation.trimOrNull(category);
    }

    private Item findItem(UUID itemId, UUID orgId) {
        return itemQuery
                .findByIdAndOrganization(itemId, orgId)
                .orElseThrow(() ->
                        new NotFoundException(
                                "Item not found"));
    }

}
