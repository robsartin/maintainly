package solutions.mystuff.infrastructure.persistence;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import solutions.mystuff.domain.model.Item;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

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
}
