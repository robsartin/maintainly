package solutions.mystuff.domain.model;

import java.time.LocalDate;

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
        String notes) {
}
