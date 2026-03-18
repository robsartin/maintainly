package solutions.mystuff.application.web;

import java.math.BigDecimal;
import java.security.Principal;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import solutions.mystuff.domain.model.AppUser;
import solutions.mystuff.domain.model.Item;
import solutions.mystuff.domain.model.NotFoundException;
import solutions.mystuff.domain.model.ServiceRecord;
import solutions.mystuff.domain.model.ServiceSchedule;
import solutions.mystuff.domain.port.in.CostQuery;
import solutions.mystuff.domain.port.in.ItemQuery;
import solutions.mystuff.domain.port.in.ScheduleQuery;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(name = "Reports",
        description = "Report views and PDF exports")
public class ReportController {

    private final ItemQuery itemQuery;
    private final ScheduleQuery scheduleQuery;
    private final ControllerHelper helper;
    private final CostQuery costQuery;

    public ReportController(
            ItemQuery itemQuery,
            ScheduleQuery scheduleQuery,
            ControllerHelper helper,
            CostQuery costQuery) {
        this.itemQuery = itemQuery;
        this.scheduleQuery = scheduleQuery;
        this.helper = helper;
        this.costQuery = costQuery;
    }

    @Operation(summary = "Reports page",
            description = "Renders the reports landing"
                    + " page with a list of items for"
                    + " which history PDFs can be"
                    + " generated. Model attributes:"
                    + " items (List<Item>).",
            responses = @ApiResponse(
                    responseCode = "200",
                    description = "HTML reports page"))
    @GetMapping("/reports")
    public String reports(
            Principal principal, Model model) {
        AppUser user = helper.resolveUser(principal);
        if (!user.hasOrganization()) {
            model.addAttribute("noOrganization", true);
            return "reports";
        }
        helper.setOrgMdc(user);
        UUID orgId = user.getOrganization().getId();
        helper.addUserAttrs(user, model);
        model.addAttribute("items",
                itemQuery.findAllByOrganization(
                        orgId));
        addCostSummary(orgId, model);
        return "reports";
    }

    @Operation(summary = "Service summary PDF",
            description = "Streams a PDF listing all"
                    + " schedules due by end of the"
                    + " current or next month."
                    + " Includes item name, service"
                    + " type, vendor, due date, and"
                    + " last completed date.",
            responses = @ApiResponse(
                    responseCode = "200",
                    description = "PDF document"
                            + " download",
                    content = @Content(
                            mediaType = "application"
                                    + "/pdf")))
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
        UUID orgId = user.getOrganization().getId();
        LocalDate cutoff = dueSoonCutoff();
        List<ServiceSchedule> schedules =
                filterDueSoon(orgId, cutoff);
        String orgName = user.getOrganization()
                .getName();
        ServiceSummaryPdf.write(
                response, schedules, cutoff,
                orgName, user.getUsername());
    }

    @Operation(summary = "Item history PDF",
            description = "Streams a PDF of the full"
                    + " service history for a single"
                    + " item. Includes all service"
                    + " records (date, type, summary,"
                    + " vendor, tech) and active"
                    + " schedules.",
            responses = {
                    @ApiResponse(responseCode = "200",
                            description = "PDF document"
                                    + " download",
                            content = @Content(
                                    mediaType
                                            = "application"
                                            + "/pdf")),
                    @ApiResponse(responseCode = "404",
                            description = "Item not"
                                    + " found")})
    @GetMapping("/reports/item-history")
    public void itemHistory(
            @Parameter(description = "Item UUID for"
                    + " which to generate the history"
                    + " report")
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
        writeItemHistoryPdf(
                user, itemId, response);
    }

    private void writeItemHistoryPdf(
            AppUser user, UUID itemId,
            HttpServletResponse response)
            throws Exception {
        UUID orgId = user.getOrganization().getId();
        Item item = itemQuery
                .findByIdAndOrganization(itemId, orgId)
                .orElseThrow(() ->
                        new NotFoundException(
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

    private void addCostSummary(
            UUID orgId, Model model) {
        BigDecimal allTime =
                costQuery.totalSpendAllTime(orgId);
        model.addAttribute("totalSpendAllTime", allTime);
        int year = LocalDate.now().getYear();
        model.addAttribute("currentYear", year);
        if (allTime.signum() == 0) {
            model.addAttribute("totalSpendThisYear",
                    BigDecimal.ZERO);
            model.addAttribute("topItemsByCost",
                    Collections.emptyList());
            return;
        }
        model.addAttribute("totalSpendThisYear",
                costQuery.totalSpendForYear(orgId, year));
        model.addAttribute("topItemsByCost",
                costQuery.topItemsByCost(orgId, 5));
    }
}
