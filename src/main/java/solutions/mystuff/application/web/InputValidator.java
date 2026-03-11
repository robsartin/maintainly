package solutions.mystuff.application.web;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;

public final class InputValidator {

    private InputValidator() {
    }

    static String requireNotBlank(
            String value, String fieldName) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException(
                    fieldName + " is required");
        }
        return value.trim();
    }

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

    static void requirePositive(
            int value, String fieldName) {
        if (value < 1) {
            throw new IllegalArgumentException(
                    fieldName + " must be at least 1");
        }
    }

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

    static void validateScheduleFields(
            String serviceType,
            int frequencyInterval) {
        requireNotBlank(serviceType, "Service type");
        requireMaxLength(
                serviceType, "Service type", 150);
        requirePositive(
                frequencyInterval, "Frequency interval");
    }
}
