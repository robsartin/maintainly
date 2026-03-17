package solutions.mystuff.application.web;

import java.time.LocalDate;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

@DisplayName("PdfHelper")
class PdfHelperTest {

    private final LocalDate today = LocalDate.of(2026, 3, 16);
    private final LocalDate soon = today.plusWeeks(2);

    @Test
    @DisplayName("should return overdue color when past due")
    void shouldReturnOverdueWhenPastDue() {
        assertEquals(PdfHelper.ROW_OVERDUE,
                PdfHelper.rowColor(
                        today.minusDays(1), today, soon));
    }

    @Test
    @DisplayName("should return soon color when within 2 weeks")
    void shouldReturnSoonWhenWithinTwoWeeks() {
        assertEquals(PdfHelper.ROW_SOON,
                PdfHelper.rowColor(
                        today.plusDays(7), today, soon));
    }

    @Test
    @DisplayName("should return ok color when beyond 2 weeks")
    void shouldReturnOkWhenBeyondTwoWeeks() {
        assertEquals(PdfHelper.ROW_OK,
                PdfHelper.rowColor(
                        soon.plusDays(1), today, soon));
    }

    @Test
    @DisplayName("should return null when date is null")
    void shouldReturnNullWhenDateNull() {
        assertNull(PdfHelper.rowColor(
                null, today, soon));
    }

    @Test
    @DisplayName("should return soon for today itself")
    void shouldReturnSoonForToday() {
        assertEquals(PdfHelper.ROW_SOON,
                PdfHelper.rowColor(
                        today, today, soon));
    }

    @Test
    @DisplayName("should return soon at boundary")
    void shouldReturnSoonAtBoundary() {
        assertEquals(PdfHelper.ROW_SOON,
                PdfHelper.rowColor(
                        soon.minusDays(1), today, soon));
    }

    @Test
    @DisplayName("should return ok at soon boundary")
    void shouldReturnOkAtSoonBoundary() {
        assertEquals(PdfHelper.ROW_OK,
                PdfHelper.rowColor(
                        soon, today, soon));
    }

    @Test
    @DisplayName("overdue color should be darker than soon in grayscale")
    void shouldHaveDistinctGrayscaleValues() {
        int overdueGray = grayscale(PdfHelper.ROW_OVERDUE);
        int soonGray = grayscale(PdfHelper.ROW_SOON);
        int okGray = grayscale(PdfHelper.ROW_OK);
        // Overdue should be darkest (lowest value)
        // OK should be lightest (highest value)
        assertEquals(true, overdueGray < soonGray,
                "overdue should be darker than soon");
        assertEquals(true, soonGray < okGray,
                "soon should be darker than ok");
    }

    private int grayscale(java.awt.Color c) {
        return (int) (0.299 * c.getRed()
                + 0.587 * c.getGreen()
                + 0.114 * c.getBlue());
    }
}
