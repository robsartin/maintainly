package solutions.mystuff.domain.model;

/**
 * Value object carrying the mutable fields of a {@link Facility}.
 * Used as a parameter object for create and update operations.
 *
 * <div class="mermaid">
 * classDiagram
 *     class FacilityData {
 *         String name
 *         String addressLine1
 *         String addressLine2
 *         String city
 *         String stateProvince
 *         String postalCode
 *         String country
 *     }
 *     FacilityManagement ..&gt; FacilityData : uses
 *     Facility ..&gt; FacilityData : populated from
 * </div>
 *
 * @see Facility
 */
public record FacilityData(
        String name,
        String addressLine1,
        String addressLine2,
        String city,
        String stateProvince,
        String postalCode,
        String country) {
}
