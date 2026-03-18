package solutions.mystuff.domain.model;

import java.math.BigDecimal;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions
        .assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions
        .assertThrows;

@DisplayName("Validation")
class ValidationTest {

    // --- requireYearInRange ---

    @Test
    @DisplayName("should accept null year")
    void shouldAcceptNullYear() {
        assertDoesNotThrow(() ->
                Validation.requireYearInRange(
                        null, "Model year"));
    }

    @Test
    @DisplayName("should accept year at lower bound")
    void shouldAcceptYearAtLowerBound() {
        assertDoesNotThrow(() ->
                Validation.requireYearInRange(
                        1900, "Model year"));
    }

    @Test
    @DisplayName("should accept year at upper bound")
    void shouldAcceptYearAtUpperBound() {
        assertDoesNotThrow(() ->
                Validation.requireYearInRange(
                        2100, "Model year"));
    }

    @Test
    @DisplayName("should accept year in valid range")
    void shouldAcceptYearInValidRange() {
        assertDoesNotThrow(() ->
                Validation.requireYearInRange(
                        2024, "Model year"));
    }

    @Test
    @DisplayName("should reject year below lower bound")
    void shouldRejectYearBelowLowerBound() {
        assertThrows(
                IllegalArgumentException.class,
                () -> Validation.requireYearInRange(
                        1899, "Model year"));
    }

    @Test
    @DisplayName("should reject year above upper bound")
    void shouldRejectYearAboveUpperBound() {
        assertThrows(
                IllegalArgumentException.class,
                () -> Validation.requireYearInRange(
                        2101, "Model year"));
    }

    // --- requireNonNegative ---

    @Test
    @DisplayName("should accept null cost")
    void shouldAcceptNullCost() {
        assertDoesNotThrow(() ->
                Validation.requireNonNegative(
                        null, "Cost"));
    }

    @Test
    @DisplayName("should accept zero cost")
    void shouldAcceptZeroCost() {
        assertDoesNotThrow(() ->
                Validation.requireNonNegative(
                        BigDecimal.ZERO, "Cost"));
    }

    @Test
    @DisplayName("should accept positive cost")
    void shouldAcceptPositiveCost() {
        assertDoesNotThrow(() ->
                Validation.requireNonNegative(
                        new BigDecimal("99.99"),
                        "Cost"));
    }

    @Test
    @DisplayName("should reject negative cost")
    void shouldRejectNegativeCost() {
        assertThrows(
                IllegalArgumentException.class,
                () -> Validation.requireNonNegative(
                        new BigDecimal("-0.01"),
                        "Cost"));
    }
}
