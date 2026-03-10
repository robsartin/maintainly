package solutions.mystuff.domain.model;

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
}
