package solutions.mystuff.application.web;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;

import solutions.mystuff.domain.model.Validation;

/**
 * Static validation utility for controller request parameters.
 * Delegates to {@link Validation} for generic checks and adds
 * web-specific helpers like date parsing.
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
        return Validation.requireNotBlank(
                value, fieldName);
    }

    /** Validates that the value does not exceed the maximum length. */
    static void requireMaxLength(
            String value, String fieldName,
            int maxLength) {
        Validation.requireMaxLength(
                value, fieldName, maxLength);
    }

    /** Validates that the integer value is at least 1. */
    static void requirePositive(
            int value, String fieldName) {
        Validation.requirePositive(value, fieldName);
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
