package solutions.mystuff.application.web;

import java.security.Principal;
import java.util.List;
import java.util.UUID;

import solutions.mystuff.domain.model.AppUser;
import solutions.mystuff.domain.model.Item;
import solutions.mystuff.domain.model.ServiceRecord;
import solutions.mystuff.domain.model.ServiceSchedule;
import solutions.mystuff.domain.port.out.ItemRepository;
import solutions.mystuff.domain.port.out
        .ServiceRecordRepository;
import solutions.mystuff.domain.port.out
        .ServiceScheduleRepository;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation
        .RequestParam;

@Controller
public class ReportController {

    private final ServiceScheduleRepository scheduleRepo;
    private final ServiceRecordRepository recordRepo;
    private final ItemRepository itemRepository;
    private final ControllerHelper helper;

    public ReportController(
            ServiceScheduleRepository scheduleRepo,
            ServiceRecordRepository recordRepo,
            ItemRepository itemRepository,
            ControllerHelper helper) {
        this.scheduleRepo = scheduleRepo;
        this.recordRepo = recordRepo;
        this.itemRepository = itemRepository;
        this.helper = helper;
    }

    @GetMapping("/reports")
    public String reports(
            Principal principal, Model model) {
        AppUser user = helper.resolveUser(principal);
        if (!user.hasOrganization()) {
            model.addAttribute("noOrganization", true);
            return "reports";
        }
        helper.setOrgMdc(user);
        try {
            UUID orgId = user.getOrganization().getId();
            helper.addUserAttrs(user, model);
            model.addAttribute("items",
                    itemRepository
                            .findByOrganizationId(
                                    orgId));
            return "reports";
        } finally {
            helper.clearOrgMdc();
        }
    }

    @GetMapping("/reports/service-summary")
    public void serviceSummary(
            Principal principal,
            HttpServletResponse response)
            throws Exception {
        AppUser user = helper.resolveUser(principal);
        helper.setOrgMdc(user);
        try {
            UUID orgId = user.getOrganization().getId();
            List<ServiceSchedule> schedules =
                    scheduleRepo
                            .findActiveByOrganizationId(
                                    orgId);
            String orgName = user.getOrganization()
                    .getName();
            ServiceSummaryPdf.write(
                    response, schedules, orgName);
        } finally {
            helper.clearOrgMdc();
        }
    }

    @GetMapping("/reports/item-history")
    public void itemHistory(
            @RequestParam UUID itemId,
            Principal principal,
            HttpServletResponse response)
            throws Exception {
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
            List<ServiceRecord> records =
                    recordRepo
                            .findByItemIdAndOrganizationId(
                                    itemId, orgId);
            List<ServiceSchedule> schedules =
                    scheduleRepo
                            .findByItemIdAndOrganizationId(
                                    itemId, orgId);
            String orgName = user.getOrganization()
                    .getName();
            ItemHistoryPdf.write(response, item,
                    records, schedules, orgName);
        } finally {
            helper.clearOrgMdc();
        }
    }
}
