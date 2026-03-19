package solutions.mystuff.domain.model;

import java.math.BigDecimal;
import java.util.regex.Pattern;

/**
 * Shared validation helpers usable by both domain services
 * and the web layer, eliminating duplicated checks.
 *
 * <div class="mermaid">
 * classDiagram
 *     class Validation {
 *         +requireNotBlank(String, String) String
 *         +requireMaxLength(String, String, int) void
 *         +trimOrNull(String) String
 *         +requireValidEmail(String, String) void
 *         +requirePositive(int, String) void
 *         +requireYearInRange(Integer, String) void
 *         +requireNonNegative(BigDecimal, String) void
 *     }
 * </div>
 */
public final class Validation {

    private static final Pattern EMAIL_PATTERN =
            Pattern.compile(
                    "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$");

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

    /** Validates that the value is a well-formed email address, if non-null. */
    public static void requireValidEmail(
            String value, String fieldName) {
        if (value != null && !value.isBlank()
                && !EMAIL_PATTERN.matcher(value.trim())
                        .matches()) {
            throw new IllegalArgumentException(
                    fieldName + " is not a valid"
                            + " email address");
        }
    }

    /** Validates that the integer value is at least 1. */
    public static void requirePositive(
            int value, String fieldName) {
        if (value < 1) {
            throw new IllegalArgumentException(
                    fieldName + " must be at least 1");
        }
    }

    private static final int MIN_YEAR = 1900;
    private static final int MAX_YEAR = 2100;

    /** Validates that the year is between 1900 and 2100 inclusive; null is allowed. */
    public static void requireYearInRange(
            Integer year, String fieldName) {
        if (year != null
                && (year < MIN_YEAR || year > MAX_YEAR)) {
            throw new IllegalArgumentException(
                    fieldName
                            + " must be between "
                            + MIN_YEAR + " and "
                            + MAX_YEAR);
        }
    }

    /** Validates that the value is not negative; null is allowed. */
    public static void requireNonNegative(
            BigDecimal value, String fieldName) {
        if (value != null
                && value.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException(
                    fieldName
                            + " must not be negative");
        }
    }
}
