package solutions.mystuff.infrastructure.persistence;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import solutions.mystuff.domain.model.ServiceSchedule;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

/**
 * Internal Spring Data repository for {@link ServiceSchedule} persistence.
 *
 * <div class="mermaid">
 * classDiagram
 *     class SpringDataScheduleRepository
 *     class JpaRepository~ServiceSchedule, UUID~
 *     SpringDataScheduleRepository --|> JpaRepository~ServiceSchedule, UUID~
 * </div>
 *
 * @see JpaScheduleRepositoryAdapter
 * @see ServiceSchedule
 */
interface SpringDataScheduleRepository
        extends JpaRepository<ServiceSchedule, UUID> {

    @Query("SELECT s FROM ServiceSchedule s "
            + "JOIN FETCH s.item "

            + "LEFT JOIN FETCH s.preferredVendor "
            + "WHERE s.organizationId = :orgId "
            + "ORDER BY s.nextDueDate ASC NULLS LAST")
    List<ServiceSchedule>
            findByOrgIdOrderByNextDueDate(
                    @Param("orgId") UUID organizationId);

    @Query("SELECT s FROM ServiceSchedule s "
            + "JOIN FETCH s.item "

            + "LEFT JOIN FETCH s.preferredVendor "
            + "WHERE s.organizationId = :orgId "
            + "AND s.active = true "
            + "ORDER BY s.nextDueDate ASC NULLS LAST")
    List<ServiceSchedule> findActiveByOrgId(
            @Param("orgId") UUID organizationId);

    @Query(value = "SELECT s FROM ServiceSchedule s "
            + "JOIN FETCH s.item "

            + "LEFT JOIN FETCH s.preferredVendor "
            + "WHERE s.organizationId = :orgId "
            + "AND s.active = true "
            + "ORDER BY s.nextDueDate ASC NULLS LAST",
            countQuery = "SELECT count(s) "
            + "FROM ServiceSchedule s "
            + "WHERE s.organizationId = :orgId "
            + "AND s.active = true")
    Page<ServiceSchedule> findActiveByOrgId(
            @Param("orgId") UUID organizationId,
            Pageable pageable);

    Optional<ServiceSchedule> findByIdAndOrganizationId(
            UUID id, UUID organizationId);

    @Query("SELECT s FROM ServiceSchedule s "

            + "LEFT JOIN FETCH s.preferredVendor "
            + "WHERE s.item.id = :itemId "
            + "AND s.organizationId = :orgId "
            + "ORDER BY s.nextDueDate ASC NULLS LAST")
    List<ServiceSchedule>
            findByItemIdAndOrgId(
                    @Param("itemId") UUID itemId,
                    @Param("orgId") UUID organizationId);
}
