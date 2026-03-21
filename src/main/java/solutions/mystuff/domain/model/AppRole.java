package solutions.mystuff.domain.model;

/**
 * Application-level roles controlling access to features.
 *
 * <p>Roles are ordered by privilege from highest to lowest:
 * {@code ADMIN} &gt; {@code FACILITY_MANAGER} &gt;
 * {@code TECHNICIAN} &gt; {@code VIEWER}.
 *
 * <div class="mermaid">
 * classDiagram
 *     class AppRole {
 *         ADMIN
 *         FACILITY_MANAGER
 *         TECHNICIAN
 *         VIEWER
 *     }
 *     AppUser --> AppRole
 *     UserGroup --> AppRole
 * </div>
 *
 * @see AppUser
 * @see UserGroup
 */
public enum AppRole {

    /** Full access to all features including user management. */
    ADMIN,

    /** Can manage items, schedules, vendors, and service records. */
    FACILITY_MANAGER,

    /** Can log service records but cannot delete items. */
    TECHNICIAN,

    /** Read-only access; cannot create, update, or delete. */
    VIEWER
}
