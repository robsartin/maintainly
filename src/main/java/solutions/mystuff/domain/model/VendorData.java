package solutions.mystuff.domain.model;

/**
 * Value object carrying vendor field values for create and
 * update operations, avoiding long parameter lists on ports.
 *
 * @see Vendor
 */
public record VendorData(
        String name,
        String phone,
        String email,
        String addressLine1,
        String addressLine2,
        String city,
        String stateProvince,
        String postalCode,
        String country,
        String website,
        String notes) {
}
