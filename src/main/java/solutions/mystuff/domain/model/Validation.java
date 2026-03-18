package solutions.mystuff.domain.model;

/**
 * Shared validation helpers usable by both domain services
 * and the web layer, eliminating duplicated checks.
 */
public final class Validation {

    private Validation() {
    }

    /** Validates that the value is not null or blank; returns trimmed value. */
    public static String requireNotBlank(
            String value, String fieldName) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException(
                    fieldName + " is required");
        }
        return value.trim();
    }

    /** Validates that the trimmed value does not exceed the maximum length. */
    public static void requireMaxLength(
            String value, String fieldName,
            int maxLength) {
        if (value != null
                && value.trim().length() > maxLength) {
            throw new IllegalArgumentException(
                    fieldName
                            + " exceeds maximum length of "
                            + maxLength);
        }
    }

    /** Returns the trimmed value, or null if blank/null. */
    public static String trimOrNull(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }
        return value.trim();
    }

    /** Validates that the integer value is at least 1. */
    public static void requirePositive(
            int value, String fieldName) {
        if (value < 1) {
            throw new IllegalArgumentException(
                    fieldName + " must be at least 1");
        }
    }
}
