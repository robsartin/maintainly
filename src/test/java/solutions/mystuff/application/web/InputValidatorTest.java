package solutions.mystuff.application.web;

import java.time.LocalDate;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions
        .assertThatThrownBy;

@DisplayName("InputValidator")
class InputValidatorTest {

    @Test
    @DisplayName("should reject blank required field")
    void shouldRejectBlankRequired() {
        assertThatThrownBy(() ->
                InputValidator.requireNotBlank(
                        "  ", "Name"))
                .isInstanceOf(
                        IllegalArgumentException.class)
                .hasMessage("Name is required");
    }

    @Test
    @DisplayName("should reject null required field")
    void shouldRejectNullRequired() {
        assertThatThrownBy(() ->
                InputValidator.requireNotBlank(
                        null, "Name"))
                .isInstanceOf(
                        IllegalArgumentException.class)
                .hasMessage("Name is required");
    }

    @Test
    @DisplayName("should return trimmed value")
    void shouldReturnTrimmedNonBlank() {
        String result = InputValidator
                .requireNotBlank("  valid  ", "Name");
        assertThat(result).isEqualTo("valid");
    }

    @Test
    @DisplayName("should reject value exceeding max"
            + " length")
    void shouldRejectExceedingMaxLength() {
        String longValue = "x".repeat(201);
        assertThatThrownBy(() ->
                InputValidator.requireMaxLength(
                        longValue, "Name", 200))
                .isInstanceOf(
                        IllegalArgumentException.class)
                .hasMessageContaining(
                        "exceeds maximum length");
    }

    @Test
    @DisplayName("should allow null for max length")
    void shouldAllowNullMaxLength() {
        InputValidator.requireMaxLength(
                null, "Name", 200);
    }

    @Test
    @DisplayName("should reject zero interval")
    void shouldRejectZeroInterval() {
        assertThatThrownBy(() ->
                InputValidator.requirePositive(
                        0, "Interval"))
                .isInstanceOf(
                        IllegalArgumentException.class)
                .hasMessageContaining("at least 1");
    }

    @Test
    @DisplayName("should parse valid date string")
    void shouldParseValidDate() {
        LocalDate result = InputValidator.parseDate(
                "2026-03-10", "Date");
        assertThat(result).isEqualTo(
                LocalDate.of(2026, 3, 10));
    }

    @Test
    @DisplayName("should reject invalid date format")
    void shouldRejectInvalidDate() {
        assertThatThrownBy(() ->
                InputValidator.parseDate(
                        "not-a-date", "Date"))
                .isInstanceOf(
                        IllegalArgumentException.class)
                .hasMessageContaining("valid date");
    }

    @Test
    @DisplayName("should reject blank date")
    void shouldRejectBlankDate() {
        assertThatThrownBy(() ->
                InputValidator.parseDate("", "Date"))
                .isInstanceOf(
                        IllegalArgumentException.class)
                .hasMessage("Date is required");
    }

}
