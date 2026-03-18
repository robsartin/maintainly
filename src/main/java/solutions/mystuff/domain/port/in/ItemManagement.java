package solutions.mystuff.domain.port.in;

import java.util.UUID;

import solutions.mystuff.domain.model.Item;
import solutions.mystuff.domain.model.ItemSpec;

/**
 * Inbound port for creating and managing items.
 *
 * <div class="mermaid">
 * classDiagram
 *     class ItemManagement {
 *         +createItem(UUID, ItemSpec) Item
 *         +updateItem(UUID, UUID, ItemSpec) Item
 *         +deleteItem(UUID, UUID) void
 *     }
 *     ItemManagementService ..|> ItemManagement
 * </div>
 *
 * @see solutions.mystuff.domain.model.Item
 */
public interface ItemManagement {

    /** Validate inputs and create a new item for the organization. */
    Item createItem(UUID orgId, ItemSpec spec);

    /** Validate inputs and update an existing item. */
    Item updateItem(UUID orgId, UUID itemId,
            ItemSpec spec);

    /** Delete an item by ID within an organization. */
    void deleteItem(UUID orgId, UUID itemId);

}
