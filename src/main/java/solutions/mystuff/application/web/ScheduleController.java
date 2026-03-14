package solutions.mystuff.application.web;

import java.security.Principal;
import java.time.LocalDate;
import java.util.Collections;
import java.util.UUID;

import solutions.mystuff.domain.model.AppUser;
import solutions.mystuff.domain.model.FrequencyUnit;
import solutions.mystuff.domain.model.PageResult;
import solutions.mystuff.domain.model.ServiceSchedule;
import solutions.mystuff.domain.model.Vendor;
import solutions.mystuff.domain.port.in.ScheduleLifecycle;
import solutions.mystuff.domain.port.in.ScheduleQuery;
import solutions.mystuff.domain.port.in.VendorManagement;
import solutions.mystuff.domain.port.in.VendorQuery;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
public class ScheduleController {

    private static final Logger log =
            LoggerFactory.getLogger(
                    ScheduleController.class);
    private static final String NEW_VENDOR_SENTINEL =
            "__new__";

    private final ControllerHelper helper;
    private final ScheduleQuery scheduleQuery;
    private final VendorManagement vendorService;
    private final VendorQuery vendorQuery;
    private final ScheduleLifecycle scheduleService;

    public ScheduleController(
            ControllerHelper helper,
            ScheduleQuery scheduleQuery,
            VendorManagement vendorService,
            VendorQuery vendorQuery,
            ScheduleLifecycle scheduleService) {
        this.helper = helper;
        this.scheduleQuery = scheduleQuery;
        this.vendorService = vendorService;
        this.vendorQuery = vendorQuery;
        this.scheduleService = scheduleService;
    }

    /** Lists active schedules with pagination. */
    @GetMapping("/schedules")
    public String schedules(
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
            loadSchedules(orgId, page, size,
                    model, response);
            helper.addUserAttrs(user, model);
            return "schedules";
        } finally {
            helper.clearOrgMdc();
        }
    }

    /** Completes a schedule by logging a service record. */
    @PostMapping("/schedules/{id}/completions")
    public String logScheduleService(
            @PathVariable("id") UUID scheduleId,
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
            @RequestParam(required = false)
                    String redirectTo,
            Principal principal) {
        AppUser user = helper.resolveUser(principal);
        helper.setOrgMdc(user);
        try {
            UUID orgId = user.getOrganization().getId();
            Vendor vendor = resolveVendor(
                    orgId, vendorId, newVendorName,
                    newVendorPhone);
            LocalDate date = InputValidator.parseDate(
                    serviceDate, "Service date");
            ServiceSchedule sched =
                    scheduleService.completeSchedule(
                            scheduleId, orgId, vendor,
                            summary, date, techName);
            if ("item".equals(redirectTo)) {
                return "redirect:/items/"
                        + sched.getItem().getId();
            }
            return "redirect:/schedules";
        } finally {
            helper.clearOrgMdc();
        }
    }

    /** Skips the current occurrence and advances the due date. */
    @PostMapping("/schedules/{id}/skip")
    public String skipSchedule(
            @PathVariable("id") UUID scheduleId,
            Principal principal) {
        AppUser user = helper.resolveUser(principal);
        helper.setOrgMdc(user);
        try {
            UUID orgId = user.getOrganization().getId();
            ServiceSchedule sched =
                    scheduleService.skipSchedule(
                            scheduleId, orgId);
            return "redirect:/items/"
                    + sched.getItem().getId();
        } finally {
            helper.clearOrgMdc();
        }
    }

    /** Deactivates (soft-deletes) a schedule. */
    @DeleteMapping("/schedules/{id}")
    public String deleteSchedule(
            @PathVariable("id") UUID scheduleId,
            Principal principal) {
        AppUser user = helper.resolveUser(principal);
        helper.setOrgMdc(user);
        try {
            UUID orgId = user.getOrganization().getId();
            scheduleService.deactivateSchedule(
                    scheduleId, orgId);
            return "redirect:/schedules";
        } finally {
            helper.clearOrgMdc();
        }
    }

    /** Creates a new recurring service schedule. */
    @PostMapping("/schedules")
    public String createSchedule(
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
            Vendor vendor = resolveVendor(
                    orgId, vendorId, newVendorName,
                    newVendorPhone);
            LocalDate due = InputValidator.parseDate(
                    nextDueDate, "Next due date");
            scheduleService.createSchedule(orgId,
                    itemId, serviceType, vendor, due,
                    frequencyInterval, frequencyUnit);
            return "redirect:/schedules";
        } finally {
            helper.clearOrgMdc();
        }
    }

    private Vendor resolveVendor(
            UUID orgId, String vendorId,
            String newVendorName,
            String newVendorPhone) {
        if (NEW_VENDOR_SENTINEL.equals(vendorId)) {
            return vendorService.createVendor(
                    orgId, newVendorName,
                    newVendorPhone);
        }
        if (vendorId != null && !vendorId.isBlank()) {
            return vendorQuery.findAllVendors(orgId)
                    .stream()
                    .filter(v -> v.getId().toString()
                            .equals(vendorId))
                    .findFirst()
                    .orElseThrow(() ->
                            new IllegalArgumentException(
                                    "Vendor not found"));
        }
        return null;
    }

    private String handleNoOrg(
            AppUser user, Model model) {
        log.warn("User {} has no organization",
                user.getUsername());
        model.addAttribute("noOrganization", true);
        model.addAttribute("schedules",
                Collections.emptyList());
        return "schedules";
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
