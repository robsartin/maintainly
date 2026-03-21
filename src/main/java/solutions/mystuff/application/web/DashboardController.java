package solutions.mystuff.application.web;

import java.security.Principal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import solutions.mystuff.domain.model.AppUser;
import solutions.mystuff.domain.model.Facility;
import solutions.mystuff.domain.port.in.DashboardQuery;
import solutions.mystuff.domain.port.in.FacilityQuery;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * Serves the dashboard home page with at-a-glance status.
 *
 * <p>Supports an optional {@code facilityId} query parameter
 * to scope statistics to a single facility. When omitted,
 * org-wide totals are shown.
 *
 * <div class="mermaid">
 * sequenceDiagram
 *     Browser->>DashboardController: GET /?facilityId=...
 *     DashboardController->>ControllerHelper: resolveUser(principal)
 *     DashboardController->>FacilityQuery: findByOrganization(...)
 *     DashboardController->>DashboardQuery: countOverdue*(...)
 *     DashboardController->>DashboardQuery: countDueSoon*(...)
 *     DashboardController->>DashboardQuery: countItems*(...)
 *     DashboardController->>DashboardQuery: findRecent*(...)
 *     DashboardController->>DashboardQuery: findFacilitySummaries(...)
 *     DashboardController-->>Browser: dashboard view
 * </div>
 *
 * @see DashboardQuery
 * @see FacilityQuery
 * @see ControllerHelper
 */
@Controller
@Tag(name = "Dashboard",
        description = "Dashboard home page")
public class DashboardController {

    private static final Logger log =
            LoggerFactory.getLogger(
                    DashboardController.class);
    private static final int RECENT_LIMIT = 5;
    private static final int DUE_SOON_DAYS = 14;

    private final ControllerHelper helper;
    private final DashboardQuery dashboardQuery;
    private final FacilityQuery facilityQuery;

    /** Creates the controller with its dependencies. */
    public DashboardController(
            ControllerHelper helper,
            DashboardQuery dashboardQuery,
            FacilityQuery facilityQuery) {
        this.helper = helper;
        this.dashboardQuery = dashboardQuery;
        this.facilityQuery = facilityQuery;
    }

    /**
     * Renders the dashboard home page.
     *
     * @param principal the authenticated user
     * @param facilityId optional facility filter
     * @param model the view model
     * @return the dashboard view name
     */
    @Operation(summary = "Dashboard home page",
            description = "Shows overdue count, due-soon"
                    + " count, total items, and recent"
                    + " activity. Optionally scoped to"
                    + " a facility.",
            responses = @ApiResponse(
                    responseCode = "200",
                    description = "HTML dashboard page"))
    @GetMapping("/")
    public String dashboard(
            Principal principal,
            @RequestParam(required = false)
            UUID facilityId,
            Model model) {
        AppUser user = helper.resolveUser(principal);
        if (!user.hasOrganization()) {
            return helper.handleNoOrg(
                    user, model, "dashboard");
        }
        helper.setOrgMdc(user);
        helper.addUserAttrs(user, model);
        UUID orgId = user.getOrganization().getId();
        addFacilityList(orgId, facilityId, model);
        addDashboardAttrs(orgId, facilityId, model);
        addFacilityBreakdown(orgId, model);
        return "dashboard";
    }

    private void addFacilityList(
            UUID orgId, UUID facilityId, Model model) {
        List<Facility> facilities =
                facilityQuery.findByOrganization(orgId);
        model.addAttribute("facilities", facilities);
        model.addAttribute("selectedFacilityId",
                facilityId);
        if (facilityId != null) {
            facilities.stream()
                    .filter(f -> facilityId.equals(
                            f.getId()))
                    .findFirst()
                    .ifPresent(f -> model.addAttribute(
                            "selectedFacilityName",
                            f.getName()));
        }
    }

    private void addDashboardAttrs(
            UUID orgId, UUID facilityId, Model model) {
        LocalDate today = LocalDate.now();
        LocalDate soonCutoff = today.plusDays(
                DUE_SOON_DAYS);
        log.info("Loading dashboard for org {}",
                orgId);
        if (facilityId != null) {
            addFacilityStats(
                    orgId, facilityId,
                    today, soonCutoff, model);
        } else {
            addOrgWideStats(
                    orgId, today, soonCutoff, model);
        }
    }

    private void addOrgWideStats(
            UUID orgId, LocalDate today,
            LocalDate soonCutoff, Model model) {
        model.addAttribute("overdueCount",
                dashboardQuery.countOverdueSchedules(
                        orgId, today));
        model.addAttribute("dueSoonCount",
                dashboardQuery.countDueSoonSchedules(
                        orgId, today, soonCutoff));
        model.addAttribute("totalItems",
                dashboardQuery.countItems(orgId));
        model.addAttribute("recentRecords",
                dashboardQuery.findRecentRecords(
                        orgId, RECENT_LIMIT));
    }

    private void addFacilityStats(
            UUID orgId, UUID facilityId,
            LocalDate today, LocalDate soonCutoff,
            Model model) {
        model.addAttribute("overdueCount",
                dashboardQuery.countOverdueByFacility(
                        orgId, facilityId, today));
        model.addAttribute("dueSoonCount",
                dashboardQuery.countDueSoonByFacility(
                        orgId, facilityId,
                        today, soonCutoff));
        model.addAttribute("totalItems",
                dashboardQuery.countItemsByFacility(
                        orgId, facilityId));
        model.addAttribute("recentRecords",
                dashboardQuery.findRecentByFacility(
                        orgId, facilityId,
                        RECENT_LIMIT));
    }

    private void addFacilityBreakdown(
            UUID orgId, Model model) {
        LocalDate today = LocalDate.now();
        model.addAttribute("facilitySummaries",
                dashboardQuery.findFacilitySummaries(
                        orgId, today));
    }
}
