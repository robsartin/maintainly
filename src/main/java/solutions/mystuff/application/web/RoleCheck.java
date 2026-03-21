package solutions.mystuff.application.web;

import java.security.Principal;
import java.util.List;

import solutions.mystuff.domain.model.AppRole;
import solutions.mystuff.domain.model.AppUser;
import solutions.mystuff.domain.model.UserGroup;
import solutions.mystuff.domain.port.in.GroupQuery;
import solutions.mystuff.domain.port.in.UserResolver;
import org.springframework.stereotype.Component;

/**
 * Evaluates whether the current user has one of the required roles.
 *
 * <p>Role resolution checks the user's direct role first, then
 * falls back to the highest-privilege role from group memberships.
 * Used by {@code @PreAuthorize} expressions via SpEL.
 *
 * <div class="mermaid">
 * sequenceDiagram
 *     PreAuthorize->>RoleCheck: hasAnyRole(principal, roles)
 *     RoleCheck->>UserResolver: resolveOrCreate(username)
 *     RoleCheck->>GroupQuery: findGroupsForUser(userId)
 *     RoleCheck-->>PreAuthorize: boolean
 * </div>
 *
 * @see AppRole
 * @see solutions.mystuff.domain.model.UserGroup
 */
@Component("roleCheck")
public class RoleCheck {

    private final UserResolver userResolver;
    private final GroupQuery groupQuery;

    public RoleCheck(UserResolver userResolver,
            GroupQuery groupQuery) {
        this.userResolver = userResolver;
        this.groupQuery = groupQuery;
    }

    /** Returns the effective role for the given user. */
    public AppRole effectiveRole(AppUser user) {
        if (user.getRole() != null) {
            return user.getRole();
        }
        List<UserGroup> groups =
                groupQuery.findGroupsForUser(
                        user.getId());
        AppRole best = null;
        for (UserGroup g : groups) {
            if (best == null
                    || g.getRole().ordinal()
                            < best.ordinal()) {
                best = g.getRole();
            }
        }
        return best != null ? best : AppRole.ADMIN;
    }

    /** Check if the principal has any of the given roles. */
    public boolean hasAnyRole(Principal principal,
            String... roles) {
        AppUser user = userResolver.resolveOrCreate(
                principal.getName());
        AppRole effective = effectiveRole(user);
        for (String role : roles) {
            if (effective.name().equals(role)) {
                return true;
            }
        }
        return false;
    }

    /** Check if the principal is not a VIEWER. */
    public boolean canWrite(Principal principal) {
        return hasAnyRole(principal,
                AppRole.ADMIN.name(),
                AppRole.FACILITY_MANAGER.name(),
                AppRole.TECHNICIAN.name());
    }

    /** Check if the principal can delete (ADMIN or FM). */
    public boolean canDelete(Principal principal) {
        return hasAnyRole(principal,
                AppRole.ADMIN.name(),
                AppRole.FACILITY_MANAGER.name());
    }

    /** Check if the principal is an ADMIN. */
    public boolean isAdmin(Principal principal) {
        return hasAnyRole(principal,
                AppRole.ADMIN.name());
    }
}
