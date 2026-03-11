package solutions.mystuff.domain.port.out;

import java.util.List;
import java.util.UUID;

import solutions.mystuff.domain.model.ServiceRecord;

/**
 * Outbound port for persisting and retrieving service records.
 *
 * <pre>{@code
 * classDiagram
 *     class ServiceRecordRepository {
 *         <<interface>>
 *         +findByItemIdAndOrganizationId(UUID, UUID) List~ServiceRecord~
 *         +findByOrganizationId(UUID) List~ServiceRecord~
 *         +save(ServiceRecord) ServiceRecord
 *     }
 *     JpaServiceRecordRepository ..|> ServiceRecordRepository
 * }</pre>
 *
 * @see solutions.mystuff.domain.model.ServiceRecord
 */
public interface ServiceRecordRepository {

    /** Find all service records for an item in an organization. */
    List<ServiceRecord> findByItemIdAndOrganizationId(
            UUID itemId, UUID organizationId);

    /** Find all service records for an organization. */
    List<ServiceRecord> findByOrganizationId(
            UUID organizationId);

    /** Persist a new or updated service record. */
    ServiceRecord save(ServiceRecord record);
}
