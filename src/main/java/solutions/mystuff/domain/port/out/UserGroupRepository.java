package solutions.mystuff.domain.port.out;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import solutions.mystuff.domain.model.UserGroup;

/**
 * Outbound port for persisting and retrieving user groups.
 *
 * <div class="mermaid">
 * classDiagram
 *     class UserGroupRepository {
 *         +findByOrganizationId(UUID) List~UserGroup~
 *         +findByIdAndOrganizationId(UUID, UUID) Optional~UserGroup~
 *         +save(UserGroup) UserGroup
 *         +deleteById(UUID) void
 *     }
 *     JpaUserGroupRepository ..|> UserGroupRepository
 * </div>
 *
 * @see solutions.mystuff.domain.model.UserGroup
 */
public interface UserGroupRepository {

    /** Find all groups belonging to an organization. */
    List<UserGroup> findByOrganizationId(
            UUID organizationId);

    /** Find a single group by ID scoped to an organization. */
    Optional<UserGroup> findByIdAndOrganizationId(
            UUID id, UUID organizationId);

    /** Persist a new or updated group. */
    UserGroup save(UserGroup group);

    /** Delete a group by ID. */
    void deleteById(UUID id);
}
