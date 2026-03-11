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
import solutions.mystuff.domain.port.in.VendorManagement;
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
import org.springframework.web.bind.annotation
        .RequestParam;

/**
 * Manages service schedule CRUD at /schedules endpoints.
 *
 * <div class="mermaid">
 * sequenceDiagram
 *     Browser->>ScheduleController: GET/POST /schedules/**
 *     ScheduleController->>ControllerHelper: resolveUser(principal)
 *     ScheduleController->>VendorManagement: resolveVendor()
 *     ScheduleController->>ScheduleLifecycle: create/edit/complete/deactivate
 *     ScheduleLifecycle->>ServiceScheduleRepository: persist
 *     ServiceScheduleRepository-->>ScheduleController: result
 *     ScheduleController-->>Browser: Thymeleaf view or redirect
 * </div>
 *
 * @see ControllerHelper
 * @see InputValidator
 */
@Controller
public class ScheduleController {

    private static final Logger log =
            LoggerFactory.getLogger(
                    ScheduleController.class);

    private final ServiceScheduleRepository scheduleRepo;
    private final VendorRepository vendorRepository;
    private final ControllerHelper helper;
    private final VendorManagement vendorService;
    private final ScheduleLifecycle scheduleService;

    public ScheduleController(
            ServiceScheduleRepository scheduleRepo,
            VendorRepository vendorRepository,
            ControllerHelper helper,
            VendorManagement vendorService,
            ScheduleLifecycle scheduleService) {
        this.scheduleRepo = scheduleRepo;
        this.vendorRepository = vendorRepository;
        this.helper = helper;
        this.vendorService = vendorService;
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
    @PostMapping("/schedules/log")
    public String logScheduleService(
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
            scheduleService.completeSchedule(
                    scheduleId, orgId, vendor,
                    summary, date, techName);
            return "redirect:/schedules";
        } finally {
            helper.clearOrgMdc();
        }
    }

    /** Deactivates (soft-deletes) a schedule. */
    @PostMapping("/schedules/delete")
    public String deleteSchedule(
            @RequestParam UUID scheduleId,
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
    @PostMapping("/schedules/create")
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
            Vendor vendor = vendorService.resolveVendor(
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

    /** Updates an existing schedule's fields. */
    @PostMapping("/schedules/edit")
    public String editSchedule(
            @RequestParam UUID scheduleId,
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
            scheduleService.editSchedule(scheduleId,
                    orgId, serviceType, due,
                    frequencyInterval, frequencyUnit,
                    vendor);
            return "redirect:/schedules";
        } finally {
            helper.clearOrgMdc();
        }
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
                scheduleRepo.findActiveByOrganizationId(
                        orgId, safePage, safeSize);
        model.addAttribute("schedules",
                result.content());
        model.addAttribute("schedPage", result);
        LinkHeaderBuilder.addLinkHeader(
                response, "/schedules", result, null);
        model.addAttribute("vendors",
                vendorRepository
                        .findByOrganizationId(orgId));
        model.addAttribute("frequencyUnits",
                FrequencyUnit.values());
        LocalDate today = LocalDate.now();
        model.addAttribute("today", today);
        model.addAttribute("soon",
                today.plusWeeks(2));
    }
}
