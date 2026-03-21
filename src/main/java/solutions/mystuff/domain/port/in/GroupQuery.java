package solutions.mystuff.domain.port.in;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import solutions.mystuff.domain.model.AppUser;
import solutions.mystuff.domain.model.UserGroup;
import solutions.mystuff.domain.model.UserGroupMembership;

/**
 * Inbound port for read-only user group queries.
 *
 * <div class="mermaid">
 * classDiagram
 *     class GroupQuery {
 *         +findAllGroups(UUID) List~UserGroup~
 *         +findGroup(UUID, UUID) Optional~UserGroup~
 *         +findMembers(UUID) List~UserGroupMembership~
 *         +findGroupsForUser(UUID) List~UserGroup~
 *     }
 * </div>
 *
 * @see solutions.mystuff.domain.model.UserGroup
 */
public interface GroupQuery {

    /** Find all groups belonging to an organization. */
    List<UserGroup> findAllGroups(UUID orgId);

    /** Find a single group by ID within an organization. */
    Optional<UserGroup> findGroup(
            UUID groupId, UUID orgId);

    /** Find all memberships for a group. */
    List<UserGroupMembership> findMembers(UUID groupId);

    /** Find all groups a user belongs to. */
    List<UserGroup> findGroupsForUser(UUID userId);

    /** Find all users in an organization for member selection. */
    List<AppUser> findOrgUsers(UUID orgId);
}
