package solutions.mystuff.domain.port.in;

import java.util.UUID;

import solutions.mystuff.domain.model.AppRole;
import solutions.mystuff.domain.model.UserGroup;

/**
 * Inbound port for user group command operations.
 *
 * <div class="mermaid">
 * classDiagram
 *     class GroupManagement {
 *         +createGroup(UUID, String, AppRole, String) UserGroup
 *         +updateGroup(UUID, UUID, String, AppRole, String) UserGroup
 *         +deleteGroup(UUID, UUID) void
 *         +addMember(UUID, UUID, UUID) void
 *         +removeMember(UUID, UUID, UUID) void
 *     }
 * </div>
 *
 * @see solutions.mystuff.domain.model.UserGroup
 */
public interface GroupManagement {

    /** Create a new user group within an organization. */
    UserGroup createGroup(UUID orgId, String name,
            AppRole role, String description);

    /** Update an existing user group. */
    UserGroup updateGroup(UUID orgId, UUID groupId,
            String name, AppRole role,
            String description);

    /** Delete a user group. */
    void deleteGroup(UUID orgId, UUID groupId);

    /** Add a user to a group. */
    void addMember(UUID orgId, UUID groupId,
            UUID userId);

    /** Remove a user from a group. */
    void removeMember(UUID orgId, UUID groupId,
            UUID userId);
}
