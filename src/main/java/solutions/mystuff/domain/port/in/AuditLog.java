package solutions.mystuff.domain.port.in;

import java.util.List;
import java.util.UUID;

import solutions.mystuff.domain.model.AuditAction;
import solutions.mystuff.domain.model.AuditEntry;

/**
 * Inbound port for recording and querying audit trail entries.
 *
 * <div class="mermaid">
 * classDiagram
 *     class AuditLog {
 *         +log(UUID, String, String, UUID, String, AuditAction, String) void
 *         +findRecentByOrganization(UUID, int) List~AuditEntry~
 *         +findByEntityId(UUID) List~AuditEntry~
 *     }
 *     AuditLogService ..|> AuditLog
 * </div>
 *
 * @see solutions.mystuff.domain.model.AuditEntry
 * @see solutions.mystuff.domain.model.AuditAction
 */
public interface AuditLog {

    /**
     * Records an audit entry.
     *
     * @param orgId      the organization ID
     * @param username   the acting user
     * @param entityType entity type (Item, Schedule, etc.)
     * @param entityId   the entity's ID
     * @param entityName human-readable entity name
     * @param action     the action performed
     * @param details    optional details (nullable)
     */
    void log(UUID orgId, String username,
             String entityType, UUID entityId,
             String entityName, AuditAction action,
             String details);

    /** Find the most recent audit entries for an organization. */
    List<AuditEntry> findRecentByOrganization(
            UUID orgId, int limit);

    /** Find all audit entries for a specific entity. */
    List<AuditEntry> findByEntityId(UUID entityId);
}
