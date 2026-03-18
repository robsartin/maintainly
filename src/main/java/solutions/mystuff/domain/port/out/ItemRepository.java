package solutions.mystuff.domain.port.out;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import solutions.mystuff.domain.model.Item;
import solutions.mystuff.domain.model.PageResult;

/**
 * Outbound port for querying and persisting items.
 *
 * <div class="mermaid">
 * classDiagram
 *     class ItemRepository {
 *         +findByOrganizationId(UUID) List~Item~
 *         +findByOrganizationId(UUID, int, int) PageResult~Item~
 *         +searchByOrganizationId(UUID, String) List~Item~
 *         +searchByOrganizationId(UUID, String, int, int) PageResult~Item~
 *         +findByIdAndOrganizationId(UUID, UUID) Optional~Item~
 *         +countByOrganizationId(UUID) long
 *         +findDistinctCategoriesByOrganizationId(UUID) List~String~
 *         +findByCategoryAndOrganizationId(UUID, String, int, int) PageResult~Item~
 *         +searchByCategoryAndOrganizationId(UUID, String, String, int, int) PageResult~Item~
 *         +save(Item) Item
 *     }
 *     JpaItemRepositoryAdapter ..|> ItemRepository
 * </div>
 *
 * @see solutions.mystuff.domain.model.Item
 */
public interface ItemRepository {

    /** Find all items belonging to an organization. */
    List<Item> findByOrganizationId(UUID organizationId);

    /** Find a page of items belonging to an organization. */
    PageResult<Item> findByOrganizationId(
            UUID organizationId, int page, int size);

    /** Search items by query within an organization. */
    List<Item> searchByOrganizationId(
            UUID organizationId, String query);

    /** Search items by query with pagination. */
    PageResult<Item> searchByOrganizationId(
            UUID organizationId, String query,
            int page, int size);

    /** Find a single item by ID scoped to an organization. */
    Optional<Item> findByIdAndOrganizationId(
            UUID id, UUID organizationId);

    /** Count all items belonging to an organization. */
    long countByOrganizationId(UUID organizationId);

    /** Find distinct categories for an organization. */
    List<String> findDistinctCategoriesByOrganizationId(
            UUID organizationId);

    /** Find a page of items filtered by category. */
    PageResult<Item> findByCategoryAndOrganizationId(
            UUID organizationId, String category,
            int page, int size);

    /** Search items by query and category with pagination. */
    PageResult<Item> searchByCategoryAndOrganizationId(
            UUID organizationId, String query,
            String category, int page, int size);

    /** Persist a new or updated item. */
    Item save(Item item);
}
