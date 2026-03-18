package solutions.mystuff.domain.port.in;

import java.time.LocalDate;
import java.util.UUID;

import solutions.mystuff.domain.model.Item;

/**
 * Inbound port for creating and managing items.
 *
 * <div class="mermaid">
 * classDiagram
 *     class ItemManagement {
 *         +createItem(UUID, String, String, String, String, String, String, Integer, String, LocalDate, String) Item
 *         +updateItem(UUID, UUID, String, String, String, String, String, Integer, String, LocalDate, String, String) Item
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
            String modelName, String serialNumber,
            String modelNumber, Integer modelYear,
            String category, LocalDate purchaseDate,
            String notes);

    /** Validate inputs and update an existing item. */
    Item updateItem(UUID orgId, UUID itemId,
            String name, String location,
            String manufacturer, String modelName,
            String modelNumber, Integer modelYear,
            String serialNumber, LocalDate purchaseDate,
            String category, String notes);

}
