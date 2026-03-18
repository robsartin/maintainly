package solutions.mystuff.application.web;

import java.security.Principal;
import java.time.LocalDate;
import java.util.Collections;
import java.util.UUID;

import solutions.mystuff.domain.model.AppUser;
import solutions.mystuff.domain.port.in.DashboardQuery;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * Serves the dashboard home page with at-a-glance status.
 *
 * <div class="mermaid">
 * sequenceDiagram
 *     Browser->>DashboardController: GET /
 *     DashboardController->>ControllerHelper: resolveUser(principal)
 *     DashboardController->>DashboardQuery: countOverdueSchedules(...)
 *     DashboardController->>DashboardQuery: countDueSoonSchedules(...)
 *     DashboardController->>DashboardQuery: countItems(...)
 *     DashboardController->>DashboardQuery: findRecentRecords(...)
 *     DashboardController-->>Browser: dashboard view
 * </div>
 *
 * @see DashboardQuery
 * @see ControllerHelper
 */
@Controller
@Tag(name = "Dashboard",
        description = "Dashboard home page")
public class DashboardController {

    private static final Logger LOG =
            LoggerFactory.getLogger(
                    DashboardController.class);
    private static final int RECENT_LIMIT = 5;
    private static final int DUE_SOON_DAYS = 14;

    private final ControllerHelper helper;
    private final DashboardQuery dashboardQuery;

    /** Creates the controller with its dependencies. */
    public DashboardController(
            ControllerHelper helper,
            DashboardQuery dashboardQuery) {
        this.helper = helper;
        this.dashboardQuery = dashboardQuery;
    }

    /**
     * Renders the dashboard home page.
     *
     * @param principal the authenticated user
     * @param model the view model
     * @return the dashboard view name
     */
    @Operation(summary = "Dashboard home page",
            description = "Shows overdue count, due-soon"
                    + " count, total items, and recent"
                    + " activity.",
            responses = @ApiResponse(
                    responseCode = "200",
                    description = "HTML dashboard page"))
    @GetMapping("/")
    public String dashboard(
            Principal principal, Model model) {
        AppUser user = helper.resolveUser(principal);
        if (!user.hasOrganization()) {
            return handleNoOrg(user, model);
        }
        helper.setOrgMdc(user);
        helper.addUserAttrs(user, model);
        addDashboardAttrs(user, model);
        return "dashboard";
    }

    private String handleNoOrg(
            AppUser user, Model model) {
        LOG.warn("User {} has no organization",
                user.getUsername());
        model.addAttribute("noOrganization", true);
        model.addAttribute("recentRecords",
                Collections.emptyList());
        return "dashboard";
    }

    private void addDashboardAttrs(
            AppUser user, Model model) {
        UUID orgId = user.getOrganization().getId();
        LocalDate today = LocalDate.now();
        LocalDate soonCutoff = today.plusDays(
                DUE_SOON_DAYS);
        LOG.info("Loading dashboard for org {}",
                orgId);
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
}
