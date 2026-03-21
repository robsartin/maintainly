package solutions.mystuff.infrastructure.persistence;

import java.util.List;
import java.util.UUID;

import solutions.mystuff.domain.model.AuditEntry;
import solutions.mystuff.domain.port.out
        .AuditEntryRepository;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository
        .JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA adapter for {@link AuditEntryRepository}.
 *
 * <div class="mermaid">
 * classDiagram
 *     class JpaAuditEntryRepository
 *     class JpaRepository~AuditEntry, UUID~
 *     class AuditEntryRepository
 *     JpaAuditEntryRepository --|&gt; JpaRepository
 *     JpaAuditEntryRepository --|&gt; AuditEntryRepository
 * </div>
 *
 * @see AuditEntryRepository
 * @see AuditEntry
 */
@Repository
public interface JpaAuditEntryRepository
        extends JpaRepository<AuditEntry, UUID>,
        AuditEntryRepository {

    /** Paginated helper for recent entries. */
    @Query("SELECT e FROM AuditEntry e "
            + "WHERE e.organizationId = :orgId "
            + "ORDER BY e.timestamp DESC")
    List<AuditEntry> findRecentByOrgId(
            @Param("orgId") UUID organizationId,
            org.springframework.data.domain
                    .Pageable pageable);

    @Override
    default List<AuditEntry> findRecentByOrganizationId(
            UUID organizationId, int limit) {
        return findRecentByOrgId(organizationId,
                PageRequest.of(0, limit));
    }

    @Override
    @Query("SELECT e FROM AuditEntry e "
            + "WHERE e.entityId = :entityId "
            + "ORDER BY e.timestamp DESC")
    List<AuditEntry> findByEntityId(
            @Param("entityId") UUID entityId);
}
