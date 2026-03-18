package solutions.mystuff.application.web.api;

import java.security.Principal;
import java.util.UUID;

import solutions.mystuff.domain.model.AppUser;
import solutions.mystuff.domain.model.PageResult;
import solutions.mystuff.domain.model.ServiceCompletion;
import solutions.mystuff.domain.model.ServiceSchedule;
import solutions.mystuff.domain.port.in.ScheduleLifecycle;
import solutions.mystuff.domain.port.in.ScheduleQuery;
import solutions.mystuff.domain.port.in.UserResolver;
import solutions.mystuff.application.web.LinkHeaderBuilder;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

/**
 * REST API for schedule operations.
 *
 * <div class="mermaid">
 * sequenceDiagram
 *     Client->>ScheduleApiController: GET /api/v1/schedules
 *     ScheduleApiController->>ScheduleQuery: findActive
 *     ScheduleApiController-->>Client: JSON PageResponse
 * </div>
 *
 * @see ScheduleQuery
 * @see ScheduleLifecycle
 */
@RestController
@RequestMapping("/api/v1/schedules")
@Tag(name = "Schedules API",
        description = "REST API for schedule management")
public class ScheduleApiController {

    private final ScheduleQuery scheduleQuery;
    private final ScheduleLifecycle scheduleService;
    private final UserResolver userResolver;

    /** Creates a schedule API controller. */
    public ScheduleApiController(
            ScheduleQuery scheduleQuery,
            ScheduleLifecycle scheduleService,
            UserResolver userResolver) {
        this.scheduleQuery = scheduleQuery;
        this.scheduleService = scheduleService;
        this.userResolver = userResolver;
    }

    /** Lists active schedules with pagination. */
    @Operation(summary = "List active schedules")
    @GetMapping
    public PageResponse<ScheduleResponse> list(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            Principal principal,
            HttpServletResponse response) {
        UUID orgId = resolveOrgId(principal);
        int clamped = Math.max(1,
                Math.min(size, 100));
        PageResult<ServiceSchedule> result =
                scheduleQuery.findActiveByOrganization(
                        orgId, page, clamped);
        LinkHeaderBuilder.addLinkHeader(
                response, "/api/v1/schedules",
                result, null);
        return PageResponse.from(
                result, ScheduleResponse::from);
    }

    /** Completes a schedule and logs a service record. */
    @Operation(summary = "Complete schedule")
    @PostMapping("/{id}/completions")
    public ScheduleResponse complete(
            @PathVariable UUID id,
            @RequestBody ServiceCompletion completion,
            Principal principal) {
        UUID orgId = resolveOrgId(principal);
        return ScheduleResponse.from(
                scheduleService.completeSchedule(
                        id, orgId, completion));
    }

    /** Skips the current occurrence. */
    @Operation(summary = "Skip schedule")
    @PostMapping("/{id}/skip")
    public ScheduleResponse skip(
            @PathVariable UUID id,
            Principal principal) {
        UUID orgId = resolveOrgId(principal);
        return ScheduleResponse.from(
                scheduleService.skipSchedule(
                        id, orgId));
    }

    /** Deactivates a schedule. */
    @Operation(summary = "Deactivate schedule")
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deactivate(
            @PathVariable UUID id,
            Principal principal) {
        UUID orgId = resolveOrgId(principal);
        scheduleService.deactivateSchedule(id, orgId);
    }

    private UUID resolveOrgId(Principal principal) {
        AppUser user = userResolver.resolveOrCreate(
                principal.getName());
        if (!user.hasOrganization()) {
            throw new IllegalArgumentException(
                    "No organization assigned");
        }
        return user.getOrganization().getId();
    }
}
