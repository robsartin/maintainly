package solutions.mystuff.application.web;

import java.math.BigDecimal;
import java.security.Principal;
import java.time.LocalDate;
import java.util.UUID;

import solutions.mystuff.domain.model.AppUser;
import solutions.mystuff.domain.model.FrequencyUnit;
import solutions.mystuff.domain.model.PageResult;
import solutions.mystuff.domain.model.ServiceCompletion;
import solutions.mystuff.domain.model.ServiceSchedule;
import solutions.mystuff.domain.model.Vendor;
import solutions.mystuff.domain.port.in.ScheduleLifecycle;
import solutions.mystuff.domain.port.in.ScheduleQuery;
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
import org.springframework.web.bind.annotation
        .RequestParam;

/**
 * Manages service schedule CRUD at /schedules endpoints.
 *
 * @see ControllerHelper
 * @see InputValidator
 */
@Controller
@Tag(name = "Schedules",
        description = "Service schedule lifecycle")
public class ScheduleController {

    private static final Logger log =
            LoggerFactory.getLogger(
                    ScheduleController.class);

    private final ControllerHelper helper;
    private final ScheduleQuery scheduleQuery;
    private final VendorQuery vendorQuery;
    private final ScheduleLifecycle scheduleService;

    public ScheduleController(
            ControllerHelper helper,
            ScheduleQuery scheduleQuery,
            VendorQuery vendorQuery,
            ScheduleLifecycle scheduleService) {
        this.helper = helper;
        this.scheduleQuery = scheduleQuery;
        this.vendorQuery = vendorQuery;
        this.scheduleService = scheduleService;
    }

    @Operation(summary = "List schedules",
            description = "Returns a paginated list of"
                    + " active schedules sorted by next"
                    + " due date. Model attributes:"
                    + " schedules"
                    + " (List<ServiceSchedule>),"
                    + " schedPage (PageResult),"
                    + " vendors (List<Vendor>),"
                    + " frequencyUnits"
                    + " (FrequencyUnit[]),"
                    + " today (LocalDate),"
                    + " soon (LocalDate, today+2"
                    + " weeks). Includes Link header"
                    + " for pagination.",
            responses = @ApiResponse(
                    responseCode = "200",
                    description = "HTML page with"
                            + " schedule table"))
    @GetMapping("/schedules")
    public String schedules(
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
                    "schedules");
        }
        helper.setOrgMdc(user);
        UUID orgId = user.getOrganization().getId();
        loadSchedules(orgId, page, size,
                model, response);
        helper.addUserAttrs(user, model);
        return "schedules";
    }

    @Operation(summary = "Complete schedule",
            description = "Logs a service record for"
                    + " the schedule and advances its"
                    + " next due date by the configured"
                    + " frequency. Vendor is optional"
                    + " for completions.",
            responses = {
                    @ApiResponse(responseCode = "302",
                            description = "Redirect to"
                                    + " /schedules (or"
                                    + " /items/{itemId}"
                                    + " if redirectTo="
                                    + "'item')"),
                    @ApiResponse(responseCode = "400",
                            description = "Validation"
                                    + " error (bad"
                                    + " date)"),
                    @ApiResponse(responseCode = "404",
                            description = "Schedule not"
                                    + " found")})
    @PostMapping("/schedules/{id}/completions")
    public String logScheduleService(
            @Parameter(description = "Schedule UUID")
            @PathVariable("id") UUID scheduleId,
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
                    + " vendor creation")
            @RequestParam(required = false)
                    String newVendorName,
            @Parameter(description = "Phone for inline"
                    + " vendor creation")
            @RequestParam(required = false)
                    String newVendorPhone,
            @Parameter(description = "Technician name")
            @RequestParam(required = false)
                    String techName,
            @Parameter(description = "Cost of the"
                    + " service (optional)")
            @RequestParam(required = false)
                    BigDecimal cost,
            @Parameter(description = "Set to 'item' to"
                    + " redirect to the item detail"
                    + " instead of /schedules")
            @RequestParam(required = false)
                    String redirectTo,
            Principal principal) {
        AppUser user = helper.resolveUser(principal);
        helper.setOrgMdc(user);
        UUID orgId = user.getOrganization().getId();
        Vendor vendor = helper.resolveVendor(
                orgId, vendorId, newVendorName,
                newVendorPhone);
        LocalDate date = InputValidator.parseDate(
                serviceDate, "Service date");
        ServiceCompletion completion =
                new ServiceCompletion(vendor, summary,
                        date, techName, cost);
        ServiceSchedule sched =
                scheduleService.completeSchedule(
                        scheduleId, orgId, completion);
        if ("item".equals(redirectTo)) {
            return "redirect:/items/"
                    + sched.getItem().getId();
        }
        return "redirect:/schedules";
    }

    @Operation(summary = "Skip schedule",
            description = "Skips the current occurrence"
                    + " and advances the next due date"
                    + " by the configured frequency"
                    + " without logging a service"
                    + " record.",
            responses = {
                    @ApiResponse(responseCode = "302",
                            description = "Redirect to"
                                    + " /items/"
                                    + "{itemId}"),
                    @ApiResponse(responseCode = "404",
                            description = "Schedule not"
                                    + " found")})
    @PostMapping("/schedules/{id}/skip")
    public String skipSchedule(
            @Parameter(description = "Schedule UUID")
            @PathVariable("id") UUID scheduleId,
            Principal principal) {
        AppUser user = helper.resolveUser(principal);
        helper.setOrgMdc(user);
        UUID orgId = user.getOrganization().getId();
        ServiceSchedule sched =
                scheduleService.skipSchedule(
                        scheduleId, orgId);
        return "redirect:/items/"
                + sched.getItem().getId();
    }

    @Operation(summary = "Delete schedule",
            description = "Soft-deletes (deactivates)"
                    + " a schedule so it no longer"
                    + " generates due dates.",
            responses = {
                    @ApiResponse(responseCode = "302",
                            description = "Redirect to"
                                    + " /schedules"),
                    @ApiResponse(responseCode = "404",
                            description = "Schedule not"
                                    + " found")})
    @DeleteMapping("/schedules/{id}")
    public String deleteSchedule(
            @Parameter(description = "Schedule UUID")
            @PathVariable("id") UUID scheduleId,
            Principal principal) {
        AppUser user = helper.resolveUser(principal);
        helper.setOrgMdc(user);
        UUID orgId = user.getOrganization().getId();
        scheduleService.deactivateSchedule(
                scheduleId, orgId);
        return "redirect:/schedules";
    }

    @Operation(summary = "Edit schedule",
            description = "Updates the service type,"
                    + " frequency, next due date, and"
                    + " preferred vendor of an existing"
                    + " schedule.",
            responses = {
                    @ApiResponse(responseCode = "302",
                            description = "Redirect to"
                                    + " /schedules"),
                    @ApiResponse(responseCode = "400",
                            description = "Validation"
                                    + " error"),
                    @ApiResponse(responseCode = "404",
                            description = "Schedule not"
                                    + " found")})
    @PostMapping("/schedules/{id}")
    public String editSchedule(
            @Parameter(description = "Schedule UUID")
            @PathVariable("id") UUID scheduleId,
            @Parameter(description = "Service type"
                    + " (required, max 150 chars)",
                    required = true)
            @RequestParam String serviceType,
            @Parameter(description = "Next due date"
                    + " in ISO format (yyyy-MM-dd)",
                    required = true)
            @RequestParam String nextDueDate,
            @Parameter(description = "Recurrence"
                    + " interval (>= 1)",
                    required = true)
            @RequestParam int frequencyInterval,
            @Parameter(description = "Recurrence unit")
            @RequestParam FrequencyUnit frequencyUnit,
            @Parameter(description = "Existing vendor"
                    + " UUID, or '__new__' to create")
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
            Principal principal) {
        AppUser user = helper.resolveUser(principal);
        helper.setOrgMdc(user);
        UUID orgId = user.getOrganization().getId();
        Vendor vendor = helper.resolveVendor(
                orgId, vendorId, newVendorName,
                newVendorPhone);
        LocalDate due = InputValidator.parseDate(
                nextDueDate, "Next due date");
        scheduleService.editSchedule(scheduleId, orgId,
                serviceType, due, frequencyInterval,
                frequencyUnit, vendor);
        return "redirect:/schedules";
    }

    private void loadSchedules(
            UUID orgId, int page, int size,
            Model model, HttpServletResponse response) {
        int safeSize = helper.clampSize(size);
        int safePage = Math.max(0, page);
        PageResult<ServiceSchedule> result =
                scheduleQuery.findActiveByOrganization(
                        orgId, safePage, safeSize);
        model.addAttribute("schedules",
                result.content());
        model.addAttribute("schedPage", result);
        LinkHeaderBuilder.addLinkHeader(
                response, "/schedules", result, null);
        model.addAttribute("vendors",
                vendorQuery.findAllVendors(orgId));
        model.addAttribute("frequencyUnits",
                FrequencyUnit.values());
        LocalDate today = LocalDate.now();
        model.addAttribute("today", today);
        model.addAttribute("soon",
                today.plusWeeks(2));
    }
}
