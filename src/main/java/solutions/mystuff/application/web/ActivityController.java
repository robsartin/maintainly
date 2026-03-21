package solutions.mystuff.application.web;

import java.security.Principal;
import java.util.UUID;

import solutions.mystuff.domain.model.AppUser;
import solutions.mystuff.domain.port.in.AuditLog;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * Displays the full audit log activity feed.
 *
 * <div class="mermaid">
 * sequenceDiagram
 *     Browser-&gt;&gt;ActivityController: GET /activity
 *     ActivityController-&gt;&gt;AuditLog: findRecentByOrganization(...)
 *     AuditLog--&gt;&gt;ActivityController: List~AuditEntry~
 *     ActivityController--&gt;&gt;Browser: activity view
 * </div>
 *
 * @see AuditLog
 * @see ControllerHelper
 */
@Controller
@Tag(name = "Activity",
        description = "Audit trail activity feed")
public class ActivityController {

    private static final int ACTIVITY_LIMIT = 50;

    private final ControllerHelper helper;
    private final AuditLog auditLog;

    /** Creates the controller with its dependencies. */
    public ActivityController(
            ControllerHelper helper,
            AuditLog auditLog) {
        this.helper = helper;
        this.auditLog = auditLog;
    }

    /**
     * Renders the full activity feed page.
     *
     * @param principal the authenticated user
     * @param model the view model
     * @return the activity view name
     */
    @Operation(summary = "Activity feed",
            description = "Shows the full audit trail"
                    + " for the organization.",
            responses = @ApiResponse(
                    responseCode = "200",
                    description = "HTML activity page"))
    @GetMapping("/activity")
    public String activity(
            Principal principal, Model model) {
        AppUser user = helper.resolveUser(principal);
        if (!user.hasOrganization()) {
            return helper.handleNoOrg(
                    user, model, "activity");
        }
        helper.setOrgMdc(user);
        helper.addUserAttrs(user, model);
        UUID orgId = user.getOrganization().getId();
        model.addAttribute("auditEntries",
                auditLog.findRecentByOrganization(
                        orgId, ACTIVITY_LIMIT));
        return "activity";
    }
}
