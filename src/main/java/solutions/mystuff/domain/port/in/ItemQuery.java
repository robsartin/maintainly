package solutions.mystuff.domain.port.in;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import solutions.mystuff.domain.model.Item;
import solutions.mystuff.domain.model.PageResult;
import solutions.mystuff.domain.model.ServiceRecord;
import solutions.mystuff.domain.model.ServiceSchedule;
import solutions.mystuff.domain.model.Vendor;

/**
 * Inbound port for read-only item queries.
 *
 * <div class="mermaid">
 * classDiagram
 *     class ItemQuery {
 *         +findByOrganization(UUID, int, int) PageResult
 *         +searchByOrganization(UUID, String, int, int) PageResult
 *         +findAllByOrganization(UUID) List
 *         +findByIdAndOrganization(UUID, UUID) Optional
 *         +findRecordsByItem(UUID, UUID) List
 *         +findSchedulesByItem(UUID, UUID) List
 *         +findVendorsByOrganization(UUID) List
 *     }
 *     ItemQueryService ..|> ItemQuery
 * </div>
 *
 * @see solutions.mystuff.domain.model.Item
 */
public interface ItemQuery {

    /** Find a page of items for an organization. */
    PageResult<Item> findByOrganization(
            UUID orgId, int page, int size);

    /** Search items by query with pagination. */
    PageResult<Item> searchByOrganization(
            UUID orgId, String query, int page, int size);

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

    /** Find all vendors for an organization. */
    List<Vendor> findVendorsByOrganization(UUID orgId);
}
