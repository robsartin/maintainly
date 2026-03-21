package solutions.mystuff.domain.service;

import java.util.List;
import java.util.UUID;

import solutions.mystuff.domain.model.AuditAction;
import solutions.mystuff.domain.model.AuditEntry;
import solutions.mystuff.domain.port.in.AuditLog;
import solutions.mystuff.domain.port.out.AuditEntryRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation
        .Transactional;

/**
 * Records and queries audit trail entries.
 *
 * <div class="mermaid">
 * sequenceDiagram
 *     participant S as DomainService
 *     participant A as AuditLogService
 *     participant R as AuditEntryRepository
 *     S-&gt;&gt;A: log(orgId, user, type, id, name, action, details)
 *     A-&gt;&gt;R: save(entry)
 * </div>
 *
 * @see AuditLog
 * @see AuditEntryRepository
 */
@Service
public class AuditLogService implements AuditLog {

    private static final Logger log =
            LoggerFactory.getLogger(
                    AuditLogService.class);

    private final AuditEntryRepository auditRepo;

    /** Creates the service with its repository dependency. */
    public AuditLogService(
            AuditEntryRepository auditRepo) {
        this.auditRepo = auditRepo;
    }

    /** {@inheritDoc} */
    @Override
    @Transactional
    public void log(UUID orgId, String username,
                    String entityType, UUID entityId,
                    String entityName,
                    AuditAction action,
                    String details) {
        AuditEntry entry = new AuditEntry();
        entry.setOrganizationId(orgId);
        entry.setUsername(username);
        entry.setEntityType(entityType);
        entry.setEntityId(entityId);
        entry.setEntityName(entityName);
        entry.setAction(action);
        entry.setDetails(details);
        auditRepo.save(entry);
        log.debug("Audit: {} {} {} '{}'",
                username, action, entityType,
                entityName);
    }

    /** {@inheritDoc} */
    @Override
    @Transactional(readOnly = true)
    public List<AuditEntry> findRecentByOrganization(
            UUID orgId, int limit) {
        return auditRepo.findRecentByOrganizationId(
                orgId, limit);
    }

    /** {@inheritDoc} */
    @Override
    @Transactional(readOnly = true)
    public List<AuditEntry> findByEntityId(
            UUID entityId) {
        return auditRepo.findByEntityId(entityId);
    }
}
