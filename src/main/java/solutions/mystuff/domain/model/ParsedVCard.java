package solutions.mystuff.domain.model;

import java.util.List;

/**
 * Typed representation of a parsed vCard contact, replacing
 * untyped {@code Map<String, Object>} returns from the parser.
 *
 * @see ParsedAltPhone
 */
public record ParsedVCard(
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
        String notes,
        List<ParsedAltPhone> altPhones) {
}
