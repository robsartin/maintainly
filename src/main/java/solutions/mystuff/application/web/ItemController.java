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
import solutions.mystuff.domain.model.ServiceCompletion;
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
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
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

    @Operation(summary = "List items",
            description = "Returns a paginated list of"
                    + " items with optional full-text"
                    + " search. Model attributes:"
                    + " items (List<Item>),"
                    + " itemPage (PageResult),"
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
            @Parameter(description = "Search query to"
                    + " filter items by name, location,"
                    + " manufacturer, model, or serial"
                    + " number")
            @RequestParam(required = false) String q,
            @Parameter(description = "Zero-based page"
                    + " index")
            @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Page size"
                    + " (max 100)")
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
        loadItems(q, orgId, page, size,
                model, response);
        helper.addUserAttrs(user, model);
        return "items";
    }

    @Operation(summary = "Item detail",
            description = "Shows the item list with"
                    + " one item expanded to show its"
                    + " service history and active"
                    + " schedules. Additional model"
                    + " attributes: selectedItemId"
                    + " (UUID), itemRecords"
                    + " (List<ServiceRecord>),"
                    + " itemSchedules"
                    + " (List<ServiceSchedule>).",
            responses = {
                    @ApiResponse(responseCode = "200",
                            description = "HTML page with"
                                    + " item detail"
                                    + " expanded"),
                    @ApiResponse(responseCode = "404",
                            description = "Item not"
                                    + " found")})
    @GetMapping("/items/{id}")
    public String itemDetail(
            @Parameter(description = "Item UUID")
            @PathVariable("id") UUID itemId,
            @Parameter(description = "Zero-based page"
                    + " index for the item list")
            @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Page size"
                    + " (max 100)")
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
        loadItems(null, orgId, page, size,
                model, response);
        helper.addUserAttrs(user, model);
        addDetailAttrs(model, itemId, orgId);
        return "items";
    }

    @Operation(summary = "Create item",
            description = "Creates a new item in the"
                    + " user's organization. Redirects"
                    + " to /items on success.",
            responses = {
                    @ApiResponse(responseCode = "302",
                            description = "Redirect to"
                                    + " /items on"
                                    + " success"),
                    @ApiResponse(responseCode = "400",
                            description = "Validation"
                                    + " error (blank"
                                    + " name, exceeds"
                                    + " max length)")})
    @PostMapping("/items")
    public String addItem(
            @Parameter(description = "Item name"
                    + " (required, max 200 chars)",
                    required = true)
            @RequestParam String name,
            @Parameter(description = "Where the item"
                    + " is located (max 200 chars)")
            @RequestParam(required = false)
                    String location,
            @Parameter(description = "Manufacturer"
                    + " name (max 200 chars)")
            @RequestParam(required = false)
                    String manufacturer,
            @Parameter(description = "Model name"
                    + " (max 200 chars)")
            @RequestParam(required = false)
                    String modelName,
            @Parameter(description = "Serial number"
                    + " (max 100 chars)")
            @RequestParam(required = false)
                    String serialNumber,
            @Parameter(description = "Model number"
                    + " (max 200 chars)")
            @RequestParam(required = false)
                    String modelNumber,
            @Parameter(description = "Model year")
            @RequestParam(required = false)
                    Integer modelYear,
            @Parameter(description = "Category"
                    + " (max 100 chars)")
            @RequestParam(required = false)
                    String category,
            @Parameter(description = "Purchase date"
                    + " in ISO format (yyyy-MM-dd)")
            @RequestParam(required = false)
                    String purchaseDate,
            @Parameter(description = "Free-text notes")
            @RequestParam(required = false) String notes,
            Principal principal) {
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
        return "redirect:/items";
    }

    @Operation(summary = "Update item",
            description = "Updates an existing item."
                    + " Redirects to /items on success.",
            responses = {
                    @ApiResponse(responseCode = "302",
                            description = "Redirect to"
                                    + " /items on"
                                    + " success"),
                    @ApiResponse(responseCode = "400",
                            description = "Validation"
                                    + " error"),
                    @ApiResponse(responseCode = "404",
                            description = "Item not"
                                    + " found")})
    @PutMapping("/items/{id}")
    public String editItem(
            @Parameter(description = "Item UUID")
            @PathVariable("id") UUID itemId,
            @Parameter(description = "Item name"
                    + " (required, max 200 chars)",
                    required = true)
            @RequestParam String name,
            @Parameter(description = "Location"
                    + " (max 200 chars)")
            @RequestParam(required = false)
                    String location,
            @Parameter(description = "Manufacturer"
                    + " (max 200 chars)")
            @RequestParam(required = false)
                    String manufacturer,
            @Parameter(description = "Model name"
                    + " (max 200 chars)")
            @RequestParam(required = false)
                    String modelName,
            @Parameter(description = "Model number"
                    + " (max 200 chars)")
            @RequestParam(required = false)
                    String modelNumber,
            @Parameter(description = "Model year")
            @RequestParam(required = false)
                    Integer modelYear,
            @Parameter(description = "Serial number"
                    + " (max 100 chars)")
            @RequestParam(required = false)
                    String serialNumber,
            @Parameter(description = "Purchase date")
            @RequestParam(required = false)
                    String purchaseDate,
            @Parameter(description = "Category"
                    + " (max 100 chars)")
            @RequestParam(required = false)
                    String category,
            @Parameter(description = "Free-text notes")
            @RequestParam(required = false) String notes,
            Principal principal) {
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
        return "redirect:/items";
    }

    @Operation(summary = "Log service record",
            description = "Logs a service visit for an"
                    + " item. If oneOff is false"
                    + " (default), advances the next"
                    + " active schedule. Vendor is"
                    + " optional for visits. Supply"
                    + " either an existing vendorId or"
                    + " '__new__' with newVendorName.",
            responses = {
                    @ApiResponse(responseCode = "302",
                            description = "Redirect to"
                                    + " /items on"
                                    + " success"),
                    @ApiResponse(responseCode = "400",
                            description = "Validation"
                                    + " error (blank"
                                    + " summary, bad"
                                    + " date)"),
                    @ApiResponse(responseCode = "404",
                            description = "Item not"
                                    + " found")})
    @PostMapping("/items/{id}/service-records")
    public String logItemService(
            @Parameter(description = "Item UUID")
            @PathVariable("id") UUID itemId,
            @Parameter(description = "What was done"
                    + " (required)", required = true)
            @RequestParam String summary,
            @Parameter(description = "Date of service"
                    + " in ISO format (yyyy-MM-dd)",
                    required = true,
                    example = "2026-06-15")
            @RequestParam String serviceDate,
            @Parameter(description = "Existing vendor"
                    + " UUID, or '__new__' to create"
                    + " inline")
            @RequestParam(required = false)
                    String vendorId,
            @Parameter(description = "Name for inline"
                    + " vendor creation (required when"
                    + " vendorId is '__new__')")
            @RequestParam(required = false)
                    String newVendorName,
            @Parameter(description = "Phone for inline"
                    + " vendor creation")
            @RequestParam(required = false)
                    String newVendorPhone,
            @Parameter(description = "Technician name")
            @RequestParam(required = false)
                    String techName,
            @Parameter(description = "If true, logs"
                    + " without advancing any schedule")
            @RequestParam(required = false,
                    defaultValue = "false")
                    boolean oneOff,
            @Parameter(description = "Cost of the"
                    + " service (optional)")
            @RequestParam(required = false)
                    BigDecimal cost,
            Principal principal) {
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
        return "redirect:/items";
    }

    @Operation(summary = "Create schedule",
            description = "Creates a recurring service"
                    + " schedule for an item. Vendor is"
                    + " required. Supply either an"
                    + " existing vendorId or '__new__'"
                    + " with newVendorName.",
            responses = {
                    @ApiResponse(responseCode = "302",
                            description = "Redirect to"
                                    + " /items (or"
                                    + " /schedules if"
                                    + " redirectTo="
                                    + "'schedules')"),
                    @ApiResponse(responseCode = "400",
                            description = "Validation"
                                    + " error (blank"
                                    + " type, bad date,"
                                    + " missing"
                                    + " vendor)")})
    @PostMapping("/items/{id}/schedules")
    public String scheduleItemService(
            @Parameter(description = "Item UUID")
            @PathVariable("id") UUID itemId,
            @Parameter(description = "Service type"
                    + " label (required, max 150"
                    + " chars)",
                    required = true,
                    example = "HVAC Inspection")
            @RequestParam String serviceType,
            @Parameter(description = "First due date"
                    + " in ISO format (yyyy-MM-dd)",
                    required = true,
                    example = "2026-09-01")
            @RequestParam String nextDueDate,
            @Parameter(description = "Recurrence"
                    + " interval (>= 1)",
                    required = true, example = "6")
            @RequestParam int frequencyInterval,
            @Parameter(description = "Recurrence unit:"
                    + " days, weeks, months, or years",
                    required = true)
            @RequestParam FrequencyUnit frequencyUnit,
            @Parameter(description = "Existing vendor"
                    + " UUID, or '__new__' to create"
                    + " inline (required)")
            @RequestParam(required = false)
                    String vendorId,
            @Parameter(description = "Name for inline"
                    + " vendor creation")
            @RequestParam(required = false)
                    String newVendorName,
            @Parameter(description = "Phone for inline"
                    + " vendor creation")
            @RequestParam(required = false)
                    String newVendorPhone,
            @Parameter(description = "Set to"
                    + " 'schedules' to redirect there"
                    + " instead of /items")
            @RequestParam(required = false)
                    String redirectTo,
            Principal principal) {
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
                        new NotFoundException(
                                "Item not found"));
    }

}
