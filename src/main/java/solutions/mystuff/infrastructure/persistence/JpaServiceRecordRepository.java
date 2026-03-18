package solutions.mystuff.infrastructure.persistence;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import solutions.mystuff.domain.model.ItemCostSummary;
import solutions.mystuff.domain.model.ServiceRecord;
import solutions.mystuff.domain.port.out.ServiceRecordRepository;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA adapter for the {@link ServiceRecordRepository} port.
 *
 * <div class="mermaid">
 * classDiagram
 *     class JpaServiceRecordRepository
 *     class JpaRepository~ServiceRecord, UUID~
 *     class ServiceRecordRepository
 *     JpaServiceRecordRepository --|> JpaRepository~ServiceRecord, UUID~
 *     JpaServiceRecordRepository --|> ServiceRecordRepository
 * </div>
 *
 * @see ServiceRecordRepository
 * @see ServiceRecord
 */
@Repository
public interface JpaServiceRecordRepository
        extends JpaRepository<ServiceRecord, UUID>,
        ServiceRecordRepository {

    @Override
    @Query("SELECT r FROM ServiceRecord r "
            + "LEFT JOIN FETCH r.vendor "
            + "WHERE r.item.id = :itemId "
            + "AND r.organizationId = :orgId "
            + "ORDER BY r.serviceDate DESC")
    List<ServiceRecord> findByItemIdAndOrganizationId(
            @Param("itemId") UUID itemId,
            @Param("orgId") UUID organizationId);

    @Override
    @Query("SELECT r FROM ServiceRecord r "
            + "JOIN FETCH r.item "
            + "LEFT JOIN FETCH r.vendor "
            + "WHERE r.organizationId = :orgId "
            + "ORDER BY r.serviceDate DESC")
    List<ServiceRecord> findByOrganizationId(
            @Param("orgId") UUID organizationId);

    @Override
    @Query("SELECT COALESCE(SUM(r.cost), 0)"
            + " FROM ServiceRecord r"
            + " WHERE r.organizationId = :orgId"
            + " AND r.serviceDate >= :from"
            + " AND r.serviceDate < :to")
    BigDecimal sumCostByOrganizationAndDateRange(
            @Param("orgId") UUID orgId,
            @Param("from") LocalDate from,
            @Param("to") LocalDate to);

    @Override
    @Query("SELECT COALESCE(SUM(r.cost), 0)"
            + " FROM ServiceRecord r"
            + " WHERE r.organizationId = :orgId")
    BigDecimal sumCostByOrganization(
            @Param("orgId") UUID orgId);

    /** Paginated helper for top items by cost. */
    @Query("SELECT new solutions.mystuff.domain.model"
            + ".ItemCostSummary(r.item.id, r.item.name,"
            + " SUM(r.cost))"
            + " FROM ServiceRecord r"
            + " WHERE r.organizationId = :orgId"
            + " GROUP BY r.item.id, r.item.name"
            + " ORDER BY SUM(r.cost) DESC")
    List<ItemCostSummary> findTopItemsByCostPage(
            @Param("orgId") UUID orgId,
            org.springframework.data.domain.Pageable page);

    @Override
    default List<ItemCostSummary> findTopItemsByCost(
            UUID orgId, int limit) {
        return findTopItemsByCostPage(orgId,
                PageRequest.of(0, limit));
    }

    /** Paginated helper for recent records. */
    @Query("SELECT r FROM ServiceRecord r "
            + "JOIN FETCH r.item "
            + "LEFT JOIN FETCH r.vendor "
            + "WHERE r.organizationId = :orgId "
            + "ORDER BY r.serviceDate DESC")
    List<ServiceRecord> findRecentByOrgId(
            @Param("orgId") UUID organizationId,
            org.springframework.data.domain.Pageable pageable);

    @Override
    default List<ServiceRecord> findRecentByOrganizationId(
            UUID organizationId, int limit) {
        return findRecentByOrgId(organizationId,
                PageRequest.of(0, limit));
    }
}
