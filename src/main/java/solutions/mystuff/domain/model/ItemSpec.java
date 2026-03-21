package solutions.mystuff.domain.model;

import java.time.LocalDate;
import java.util.UUID;

/**
 * Value object carrying the mutable fields of an {@link Item}.
 * Used as a parameter object for create and update operations.
 *
 * <div class="mermaid">
 * classDiagram
 *     class ItemSpec {
 *         String name
 *         String location
 *         String manufacturer
 *         String modelName
 *         String serialNumber
 *         String modelNumber
 *         Integer modelYear
 *         String category
 *         LocalDate purchaseDate
 *         String notes
 *         UUID facilityId
 *     }
 *     ItemManagement ..&gt; ItemSpec : uses
 *     Item ..&gt; ItemSpec : populated from
 * </div>
 *
 * @see Item
 */
public record ItemSpec(
        String name,
        String location,
        String manufacturer,
        String modelName,
        String serialNumber,
        String modelNumber,
        Integer modelYear,
        String category,
        LocalDate purchaseDate,
        String notes,
        UUID facilityId) {

    /** Constructor without facilityId for backward compatibility. */
    public ItemSpec(
            String name,
            String location,
            String manufacturer,
            String modelName,
            String serialNumber,
            String modelNumber,
            Integer modelYear,
            String category,
            LocalDate purchaseDate,
            String notes) {
        this(name, location, manufacturer, modelName,
                serialNumber, modelNumber, modelYear,
                category, purchaseDate, notes, null);
    }
}
