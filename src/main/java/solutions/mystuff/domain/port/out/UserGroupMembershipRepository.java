package solutions.mystuff.domain.port.out;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import solutions.mystuff.domain.model.UserGroup;
import solutions.mystuff.domain.model.UserGroupMembership;

/**
 * Outbound port for persisting and retrieving group memberships.
 *
 * <div class="mermaid">
 * classDiagram
 *     class UserGroupMembershipRepository {
 *         +findByGroup(UserGroup) List~UserGroupMembership~
 *         +findByUserId(UUID) List~UserGroupMembership~
 *         +findByGroupAndUserId(UserGroup, UUID) Optional~UserGroupMembership~
 *         +save(UserGroupMembership) UserGroupMembership
 *         +delete(UserGroupMembership) void
 *         +deleteByGroup(UserGroup) void
 *     }
 *     JpaUserGroupMembershipRepository ..|> UserGroupMembershipRepository
 * </div>
 *
 * @see solutions.mystuff.domain.model.UserGroupMembership
 */
public interface UserGroupMembershipRepository {

    /** Find all memberships for a group. */
    List<UserGroupMembership> findByGroup(
            UserGroup group);

    /** Find all memberships for a user. */
    List<UserGroupMembership> findByUserId(UUID userId);

    /** Find a specific membership. */
    Optional<UserGroupMembership> findByGroupAndUserId(
            UserGroup group, UUID userId);

    /** Persist a new membership. */
    UserGroupMembership save(
            UserGroupMembership membership);

    /** Delete a membership. */
    void delete(UserGroupMembership membership);

    /** Delete all memberships for a group. */
    void deleteByGroup(UserGroup group);
}
