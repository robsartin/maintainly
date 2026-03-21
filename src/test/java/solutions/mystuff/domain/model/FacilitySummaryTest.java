package solutions.mystuff.domain.model;

import java.util.UUID;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Unit tests for {@link FacilitySummary}.
 *
 * <div class="mermaid">
 * classDiagram
 *     class FacilitySummaryTest
 *     FacilitySummaryTest --> FacilitySummary
 * </div>
 */
@DisplayName("FacilitySummary")
class FacilitySummaryTest {

    @Test
    @DisplayName("should expose all fields via accessors")
    void shouldExposeAllFieldsViaAccessors() {
        UUID id = UuidV7.generate();
        FacilitySummary summary = new FacilitySummary(
                id, "Warehouse", 15L, 3L);
        assertEquals(id, summary.facilityId());
        assertEquals("Warehouse",
                summary.facilityName());
        assertEquals(15L, summary.itemCount());
        assertEquals(3L, summary.overdueCount());
    }
}
