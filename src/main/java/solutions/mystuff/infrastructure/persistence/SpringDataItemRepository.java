package solutions.mystuff.infrastructure.persistence;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import solutions.mystuff.domain.model.Item;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

/**
 * Internal Spring Data repository for {@link Item} persistence.
 *
 * <div class="mermaid">
 * classDiagram
 *     class SpringDataItemRepository
 *     class JpaRepository~Item, UUID~
 *     SpringDataItemRepository --|> JpaRepository~Item, UUID~
 * </div>
 *
 * @see JpaItemRepositoryAdapter
 * @see Item
 */
interface SpringDataItemRepository
        extends JpaRepository<Item, UUID> {

    List<Item> findByOrganizationId(UUID organizationId);

    Slice<Item> findByOrganizationId(
            UUID organizationId, Pageable pageable);

    @Query("SELECT i FROM Item i "
            + "WHERE i.organizationId = :orgId "
            + "AND (LOWER(i.name) LIKE "
            + "LOWER(CONCAT('%', :q, '%')) "
            + "OR LOWER(i.location) LIKE "
            + "LOWER(CONCAT('%', :q, '%')))")
    List<Item> searchByOrganizationId(
            @Param("orgId") UUID organizationId,
            @Param("q") String query);

    @Query("SELECT i FROM Item i "
            + "WHERE i.organizationId = :orgId "
            + "AND (LOWER(i.name) LIKE "
            + "LOWER(CONCAT('%', :q, '%')) "
            + "OR LOWER(i.location) LIKE "
            + "LOWER(CONCAT('%', :q, '%')))")
    Slice<Item> searchByOrganizationId(
            @Param("orgId") UUID organizationId,
            @Param("q") String query,
            Pageable pageable);

    Optional<Item> findByIdAndOrganizationId(
            UUID id, UUID organizationId);

    long countByOrganizationId(UUID organizationId);

    @Query("SELECT DISTINCT i.category FROM Item i "
            + "WHERE i.organizationId = :orgId "
            + "AND i.category IS NOT NULL "
            + "AND i.category <> '' "
            + "ORDER BY i.category")
    List<String> findDistinctCategoriesByOrganizationId(
            @Param("orgId") UUID organizationId);

    @Query("SELECT i FROM Item i "
            + "WHERE i.organizationId = :orgId "
            + "AND i.category = :cat")
    Slice<Item> findByCategoryAndOrganizationId(
            @Param("orgId") UUID organizationId,
            @Param("cat") String category,
            Pageable pageable);

    @Query("SELECT i FROM Item i "
            + "WHERE i.organizationId = :orgId "
            + "AND i.category = :cat "
            + "AND (LOWER(i.name) LIKE "
            + "LOWER(CONCAT('%', :q, '%')) "
            + "OR LOWER(i.location) LIKE "
            + "LOWER(CONCAT('%', :q, '%')))")
    Slice<Item> searchByCategoryAndOrganizationId(
            @Param("orgId") UUID organizationId,
            @Param("q") String query,
            @Param("cat") String category,
            Pageable pageable);

    /**
     * Bulk-deletes an item by ID and organization using JPQL
     * instead of entity removal. This avoids Hibernate loading
     * and individually deleting each child schedule and record,
     * relying on the database {@code ON DELETE CASCADE} on
     * {@code service_schedules.item_id} and
     * {@code service_records.item_id} instead.
     */
    @Modifying
    @Transactional
    @Query("DELETE FROM Item i "
            + "WHERE i.id = :id "
            + "AND i.organizationId = :orgId")
    void deleteByIdAndOrganizationId(
            @Param("id") UUID id,
            @Param("orgId") UUID organizationId);

}
