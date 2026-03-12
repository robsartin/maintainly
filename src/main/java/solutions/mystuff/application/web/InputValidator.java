package solutions.mystuff.application.web;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;

/**
 * Static validation utility for controller request parameters.
 *
 * <div class="mermaid">
 * classDiagram
 *     class InputValidator {
 *         +requireNotBlank(String, String)$ String
 *         +requireMaxLength(String, String, int)$ void
 *         +requirePositive(int, String)$ void
 *         +parseDate(String, String)$ LocalDate
 *         +validateScheduleFields(String, int)$ void
 *     }
 * </div>
 *
 * @see ItemController
 * @see ScheduleController
 */
public final class InputValidator {

    private InputValidator() {
    }

    /** Validates that the value is not null or blank. */
    static String requireNotBlank(
            String value, String fieldName) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException(
                    fieldName + " is required");
        }
        return value.trim();
    }

    /** Validates that the value does not exceed the maximum length. */
    static void requireMaxLength(
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

    /** Validates that the integer value is at least 1. */
    static void requirePositive(
            int value, String fieldName) {
        if (value < 1) {
            throw new IllegalArgumentException(
                    fieldName + " must be at least 1");
        }
    }

    /** Parses an ISO date string, throwing on invalid input. */
    static LocalDate parseDate(
            String value, String fieldName) {
        requireNotBlank(value, fieldName);
        try {
            return LocalDate.parse(value.trim());
        } catch (DateTimeParseException e) {
            throw new IllegalArgumentException(
                    fieldName
                            + " must be a valid date"
                            + " (yyyy-MM-dd)");
        }
    }

}
