package solutions.mystuff.domain.model;

/**
 * Enumeration of actions that can be recorded in the audit trail.
 *
 * <div class="mermaid">
 * classDiagram
 *     class AuditAction {
 *         &lt;&lt;enumeration&gt;&gt;
 *         CREATE
 *         UPDATE
 *         DELETE
 *         COMPLETE
 *         SKIP
 *     }
 *     AuditEntry --> AuditAction
 * </div>
 *
 * @see AuditEntry
 */
public enum AuditAction {

    /** A new entity was created. */
    CREATE,

    /** An existing entity was modified. */
    UPDATE,

    /** An entity was deleted. */
    DELETE,

    /** A schedule was completed. */
    COMPLETE,

    /** A schedule occurrence was skipped. */
    SKIP
}
