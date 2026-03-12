package solutions.mystuff.domain.port.in;

import java.util.UUID;

import solutions.mystuff.domain.model.Item;

/**
 * Inbound port for creating and managing items.
 *
 * <div class="mermaid">
 * classDiagram
 *     class ItemManagement {
 *         +createItem(UUID, String, String, String, String, String) Item
 *     }
 *     ItemManagementService ..|> ItemManagement
 * </div>
 *
 * @see solutions.mystuff.domain.model.Item
 */
public interface ItemManagement {

    /** Validate inputs and create a new item for the organization. */
    Item createItem(UUID orgId, String name,
            String location, String manufacturer,
            String modelName, String serialNumber);
}
