package solutions.mystuff.domain.model;

/**
 * A single alternate phone number parsed from a vCard TEL property.
 *
 * @see ParsedVCard
 */
public record ParsedAltPhone(String phone, String label) {
}
