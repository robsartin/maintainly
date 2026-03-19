package solutions.mystuff.domain.port.in;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import solutions.mystuff.domain.model.Item;
import solutions.mystuff.domain.model.PageRequest;
import solutions.mystuff.domain.model.PageResult;
import solutions.mystuff.domain.model.ServiceRecord;
import solutions.mystuff.domain.model.ServiceSchedule;

/**
 * Inbound port for read-only item queries.
 *
 * <div class="mermaid">
 * classDiagram
 *     class ItemQuery {
 *         +findByOrganization(UUID, PageRequest) PageResult
 *         +searchByOrganization(UUID, String, PageRequest) PageResult
 *         +findByCategoryAndOrganization(UUID, String, PageRequest) PageResult
 *         +findDistinctCategories(UUID) List~String~
 *         +findByIdAndOrganization(UUID, UUID) Optional~Item~
 *     }
 *     ItemQueryService ..|> ItemQuery
 * </div>
 *
 * @see solutions.mystuff.domain.model.Item
 */
public interface ItemQuery {

    /** Find a page of items for an organization. */
    PageResult<Item> findByOrganization(
            UUID orgId, PageRequest pageReq);

    /** Search items by query with pagination. */
    PageResult<Item> searchByOrganization(
            UUID orgId, String query,
            PageRequest pageReq);

    /** Find a page of items filtered by category. */
    PageResult<Item> findByCategoryAndOrganization(
            UUID orgId, String category,
            PageRequest pageReq);

    /** Search items by query and category. */
    PageResult<Item> searchByCategoryAndOrganization(
            UUID orgId, String query, String category,
            PageRequest pageReq);

    /** Find distinct categories for an organization. */
    List<String> findDistinctCategories(UUID orgId);

    /** Find all items for an organization. */
    List<Item> findAllByOrganization(UUID orgId);

    /** Find a single item by ID within an organization. */
    Optional<Item> findByIdAndOrganization(
            UUID itemId, UUID orgId);

    /** Find service records for an item. */
    List<ServiceRecord> findRecordsByItem(
            UUID itemId, UUID orgId);

    /** Find service schedules for an item. */
    List<ServiceSchedule> findSchedulesByItem(
            UUID itemId, UUID orgId);
}
