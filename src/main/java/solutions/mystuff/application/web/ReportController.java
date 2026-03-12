package solutions.mystuff.application.web;

import java.security.Principal;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;
import java.util.UUID;

import solutions.mystuff.domain.model.AppUser;
import solutions.mystuff.domain.model.Item;
import solutions.mystuff.domain.model.ServiceRecord;
import solutions.mystuff.domain.model.ServiceSchedule;
import solutions.mystuff.domain.port.in.ItemQuery;
import solutions.mystuff.domain.port.in.ScheduleQuery;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation
        .RequestParam;

/**
 * Serves the reports page and generates PDF exports.
 *
 * <div class="mermaid">
 * sequenceDiagram
 *     Browser->>ReportController: GET /reports/**
 *     ReportController->>ControllerHelper: resolveUser(principal)
 *     ReportController->>ItemQuery: findAllByOrganization()
 *     ReportController->>ScheduleQuery: findAllActiveByOrganization()
 *     ReportController->>ServiceSummaryPdf: write(response)
 *     ReportController->>ItemHistoryPdf: write(response)
 * </div>
 *
 * @see ServiceSummaryPdf
 * @see ItemHistoryPdf
 */
@Controller
public class ReportController {

    private final ItemQuery itemQuery;
    private final ScheduleQuery scheduleQuery;
    private final ControllerHelper helper;

    public ReportController(
            ItemQuery itemQuery,
            ScheduleQuery scheduleQuery,
            ControllerHelper helper) {
        this.itemQuery = itemQuery;
        this.scheduleQuery = scheduleQuery;
        this.helper = helper;
    }

    /** Renders the reports landing page. */
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
                    itemQuery.findAllByOrganization(
                            orgId));
            return "reports";
        } finally {
            helper.clearOrgMdc();
        }
    }

    /** Streams a PDF of schedules due within the cutoff window. */
    @GetMapping("/reports/service-summary")
    public void serviceSummary(
            Principal principal,
            HttpServletResponse response)
            throws Exception {
        AppUser user = helper.resolveUser(principal);
        if (!user.hasOrganization()) {
            response.sendError(
                    HttpServletResponse.SC_BAD_REQUEST,
                    "No organization assigned");
            return;
        }
        helper.setOrgMdc(user);
        try {
            UUID orgId = user.getOrganization().getId();
            LocalDate cutoff = dueSoonCutoff();
            List<ServiceSchedule> schedules =
                    filterDueSoon(orgId, cutoff);
            String orgName = user.getOrganization()
                    .getName();
            ServiceSummaryPdf.write(
                    response, schedules, cutoff,
                    orgName, user.getUsername());
        } finally {
            helper.clearOrgMdc();
        }
    }

    /** Streams a PDF of service history for a single item. */
    @GetMapping("/reports/item-history")
    public void itemHistory(
            @RequestParam UUID itemId,
            Principal principal,
            HttpServletResponse response)
            throws Exception {
        AppUser user = helper.resolveUser(principal);
        if (!user.hasOrganization()) {
            response.sendError(
                    HttpServletResponse.SC_BAD_REQUEST,
                    "No organization assigned");
            return;
        }
        helper.setOrgMdc(user);
        try {
            writeItemHistoryPdf(
                    user, itemId, response);
        } finally {
            helper.clearOrgMdc();
        }
    }

    private void writeItemHistoryPdf(
            AppUser user, UUID itemId,
            HttpServletResponse response)
            throws Exception {
        UUID orgId = user.getOrganization().getId();
        Item item = itemQuery
                .findByIdAndOrganization(itemId, orgId)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Item not found"));
        List<ServiceRecord> records =
                itemQuery.findRecordsByItem(
                        itemId, orgId);
        List<ServiceSchedule> schedules =
                itemQuery.findSchedulesByItem(
                        itemId, orgId);
        ItemHistoryPdf.write(response, item, records,
                schedules,
                user.getOrganization().getName(),
                user.getUsername());
    }

    private List<ServiceSchedule> filterDueSoon(
            UUID orgId, LocalDate cutoff) {
        return scheduleQuery
                .findAllActiveByOrganization(orgId)
                .stream()
                .filter(s ->
                        s.getNextDueDate() != null
                        && !s.getNextDueDate()
                                .isAfter(cutoff))
                .toList();
    }

    private LocalDate dueSoonCutoff() {
        LocalDate today = LocalDate.now();
        YearMonth target =
                today.getDayOfMonth() > 14
                        ? YearMonth.from(today)
                                .plusMonths(1)
                        : YearMonth.from(today);
        return target.atEndOfMonth();
    }
}
