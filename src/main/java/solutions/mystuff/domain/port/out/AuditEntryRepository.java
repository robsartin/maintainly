package solutions.mystuff.domain.port.out;

import java.util.List;
import java.util.UUID;

import solutions.mystuff.domain.model.AuditEntry;

/**
 * Outbound port for persisting and retrieving audit entries.
 *
 * <div class="mermaid">
 * classDiagram
 *     class AuditEntryRepository {
 *         +save(AuditEntry) AuditEntry
 *         +findRecentByOrganizationId(UUID, int) List~AuditEntry~
 *         +findByEntityId(UUID) List~AuditEntry~
 *     }
 *     JpaAuditEntryRepository ..|> AuditEntryRepository
 * </div>
 *
 * @see solutions.mystuff.domain.model.AuditEntry
 */
public interface AuditEntryRepository {

    /** Persist a new audit entry. */
    AuditEntry save(AuditEntry entry);

    /** Find the most recent audit entries for an organization. */
    List<AuditEntry> findRecentByOrganizationId(
            UUID organizationId, int limit);

    /** Find all audit entries for a specific entity. */
    List<AuditEntry> findByEntityId(UUID entityId);
}
