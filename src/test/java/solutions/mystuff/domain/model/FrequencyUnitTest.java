package solutions.mystuff.domain.model;

import java.time.LocalDate;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DisplayName("FrequencyUnit")
class FrequencyUnitTest {

    @Test
    @DisplayName("should have four values")
    void shouldHaveFourValues() {
        assertEquals(4, FrequencyUnit.values().length);
    }

    @Test
    @DisplayName("should parse from string")
    void shouldParseFromString() {
        assertEquals(FrequencyUnit.days,
                FrequencyUnit.valueOf("days"));
        assertEquals(FrequencyUnit.weeks,
                FrequencyUnit.valueOf("weeks"));
        assertEquals(FrequencyUnit.months,
                FrequencyUnit.valueOf("months"));
        assertEquals(FrequencyUnit.years,
                FrequencyUnit.valueOf("years"));
    }

    @Test
    @DisplayName("should advance by days")
    void shouldAdvanceByDays() {
        LocalDate date = LocalDate.of(2026, 3, 10);
        assertEquals(LocalDate.of(2026, 3, 24),
                FrequencyUnit.days.advance(date, 14));
    }

    @Test
    @DisplayName("should advance by weeks")
    void shouldAdvanceByWeeks() {
        LocalDate date = LocalDate.of(2026, 3, 10);
        assertEquals(LocalDate.of(2026, 3, 24),
                FrequencyUnit.weeks.advance(date, 2));
    }

    @Test
    @DisplayName("should advance by months")
    void shouldAdvanceByMonths() {
        LocalDate date = LocalDate.of(2026, 3, 10);
        assertEquals(LocalDate.of(2026, 9, 10),
                FrequencyUnit.months.advance(date, 6));
    }

    @Test
    @DisplayName("should advance by years")
    void shouldAdvanceByYears() {
        LocalDate date = LocalDate.of(2026, 3, 10);
        assertEquals(LocalDate.of(2027, 3, 10),
                FrequencyUnit.years.advance(date, 1));
    }
}
