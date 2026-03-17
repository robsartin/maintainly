package solutions.mystuff.application.web.api;

import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

import solutions.mystuff.domain.model.Item;

/**
 * Data transfer object mapping a domain {@link Item} to JSON.
 *
 * <div class="mermaid">
 * classDiagram
 *     class ItemDto {
 *         UUID id
 *         String name
 *         String location
 *         String manufacturer
 *         String modelName
 *         String serialNumber
 *         LocalDate purchaseDate
 *         Instant createdAt
 *     }
 * </div>
 *
 * @see ItemApiController
 * @see Item
 */
public record ItemDto(
        UUID id,
        String name,
        String location,
        String manufacturer,
        String modelName,
        String serialNumber,
        LocalDate purchaseDate,
        Instant createdAt) {

    /** Maps a domain Item to its API representation. */
    public static ItemDto from(Item item) {
        return new ItemDto(
                item.getId(),
                item.getName(),
                item.getLocation(),
                item.getManufacturer(),
                item.getModelName(),
                item.getSerialNumber(),
                item.getPurchaseDate(),
                item.getCreatedAt());
    }
}
