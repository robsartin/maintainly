package solutions.mystuff.application.web.api;

import java.time.LocalDate;
import java.util.UUID;

import solutions.mystuff.domain.model.Item;

/**
 * JSON response DTO for an item, avoiding lazy-load issues.
 *
 * <div class="mermaid">
 * classDiagram
 *     class ItemResponse {
 *         UUID id
 *         String name
 *         String location
 *         String manufacturer
 *         String modelName
 *         String category
 *     }
 *     ItemResponse ..&gt; Item : from
 * </div>
 *
 * @see ItemApiController
 */
public record ItemResponse(
        UUID id,
        String name,
        String location,
        String manufacturer,
        String modelName,
        String modelNumber,
        Integer modelYear,
        String serialNumber,
        String category,
        LocalDate purchaseDate,
        String notes) {

    /** Creates a response DTO from a domain Item. */
    public static ItemResponse from(Item item) {
        return new ItemResponse(
                item.getId(),
                item.getName(),
                item.getLocation(),
                item.getManufacturer(),
                item.getModelName(),
                item.getModelNumber(),
                item.getModelYear(),
                item.getSerialNumber(),
                item.getCategory(),
                item.getPurchaseDate(),
                item.getNotes());
    }
}
