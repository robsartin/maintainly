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
public class ScheduleController {

    private static final Logger log =
            LoggerFactory.getLogger(
                    ScheduleController.class);

    private final ServiceScheduleRepository scheduleRepo;
    private final ItemRepository itemRepository;
    private final VendorRepository vendorRepository;
    private final ControllerHelper helper;

    public ScheduleController(
            ServiceScheduleRepository scheduleRepo,
            ItemRepository itemRepository,
            VendorRepository vendorRepository,
            ControllerHelper helper) {
        this.scheduleRepo = scheduleRepo;
        this.itemRepository = itemRepository;
        this.vendorRepository = vendorRepository;
        this.helper = helper;
    }

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
            ServiceSchedule sched = findSchedule(
                    scheduleId, orgId);
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
            return "redirect:/schedules";
        } finally {
            helper.clearOrgMdc();
        }
    }

    @PostMapping("/schedules/delete")
    public String deleteSchedule(
            @RequestParam UUID scheduleId,
            Principal principal) {
        AppUser user = helper.resolveUser(principal);
        helper.setOrgMdc(user);
        try {
            UUID orgId = user.getOrganization().getId();
            ServiceSchedule sched = findSchedule(
                    scheduleId, orgId);
            sched.setActive(false);
            scheduleRepo.save(sched);
            log.info("Deactivated schedule {}",
                    scheduleId);
            return "redirect:/schedules";
        } finally {
            helper.clearOrgMdc();
        }
    }

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
            Item item = itemRepository
                    .findByIdAndOrganizationId(
                            itemId, orgId)
                    .orElseThrow(() ->
                            new IllegalArgumentException(
                                    "Item not found"));
            Vendor vendor = helper.resolveVendor(orgId,
                    vendorId, newVendorName,
                    newVendorPhone);
            helper.createSchedule(orgId, item,
                    serviceType.trim(), vendor,
                    nextDueDate, frequencyInterval,
                    frequencyUnit);
            return "redirect:/schedules";
        } finally {
            helper.clearOrgMdc();
        }
    }

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
            ServiceSchedule sched = findSchedule(
                    scheduleId, orgId);
            applyScheduleEdits(sched, orgId,
                    serviceType, nextDueDate,
                    frequencyInterval, frequencyUnit,
                    vendorId, newVendorName,
                    newVendorPhone);
            scheduleRepo.save(sched);
            log.info("Updated schedule {}",
                    scheduleId);
            return "redirect:/schedules";
        } finally {
            helper.clearOrgMdc();
        }
    }

    private void applyScheduleEdits(
            ServiceSchedule sched, UUID orgId,
            String serviceType, String nextDueDate,
            int frequencyInterval,
            FrequencyUnit frequencyUnit,
            String vendorId, String newVendorName,
            String newVendorPhone) {
        ControllerHelper.requireNotBlank(
                serviceType, "Service type");
        ControllerHelper.requireMaxLength(
                serviceType, "Service type", 150);
        ControllerHelper.requirePositive(
                frequencyInterval,
                "Frequency interval");
        LocalDate due = ControllerHelper.parseDate(
                nextDueDate, "Next due date");
        sched.setServiceType(serviceType.trim());
        sched.setNextDueDate(due);
        sched.setFrequencyInterval(frequencyInterval);
        sched.setFrequencyUnit(frequencyUnit);
        Vendor vendor = helper.resolveVendor(orgId,
                vendorId, newVendorName,
                newVendorPhone);
        sched.setPreferredVendor(vendor);
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
