package solutions.mystuff.domain.port.out;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import solutions.mystuff.domain.model.ItemCostSummary;
import solutions.mystuff.domain.model.ServiceRecord;

/**
 * Outbound port for persisting and retrieving service records.
 *
 * <div class="mermaid">
 * classDiagram
 *     class ServiceRecordRepository {
 *         +findByItemIdAndOrganizationId(UUID, UUID) List~ServiceRecord~
 *         +findByIdAndOrganizationId(UUID, UUID) Optional~ServiceRecord~
 *         +findByOrganizationId(UUID) List~ServiceRecord~
 *         +findRecentByOrganizationId(UUID, int) List~ServiceRecord~
 *         +findRecentByFacility(UUID, UUID, int) List~ServiceRecord~
 *         +save(ServiceRecord) ServiceRecord
 *         +deleteByIdAndOrganizationId(UUID, UUID) void
 *     }
 *     JpaServiceRecordRepository ..|> ServiceRecordRepository
 * </div>
 *
 * @see solutions.mystuff.domain.model.ServiceRecord
 */
public interface ServiceRecordRepository {

    /** Find all service records for an item in an organization. */
    List<ServiceRecord> findByItemIdAndOrganizationId(
            UUID itemId, UUID organizationId);

    /** Find a single service record by ID scoped to an organization. */
    Optional<ServiceRecord> findByIdAndOrganizationId(
            UUID id, UUID organizationId);

    /** Find all service records for an organization. */
    List<ServiceRecord> findByOrganizationId(
            UUID organizationId);

    /** Find the most recent service records for an organization. */
    List<ServiceRecord> findRecentByOrganizationId(
            UUID organizationId, int limit);

    /** Find recent records scoped to a facility. */
    List<ServiceRecord> findRecentByFacility(
            UUID organizationId, UUID facilityId,
            int limit);

    /** Persist a new or updated service record. */
    ServiceRecord save(ServiceRecord record);

    /** Delete a service record by ID scoped to an organization. */
    void deleteByIdAndOrganizationId(
            UUID id, UUID organizationId);

    /** Sum of cost for an organization in a half-open date range [from, to). */
    BigDecimal sumCostByOrganizationAndDateRange(
            UUID orgId, LocalDate from, LocalDate to);

    /** Sum of cost for an organization across all time. */
    BigDecimal sumCostByOrganization(UUID orgId);

    /** Top items by total cost, limited. */
    List<ItemCostSummary> findTopItemsByCost(
            UUID orgId, int limit);
}
