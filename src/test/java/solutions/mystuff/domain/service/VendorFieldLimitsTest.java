package solutions.mystuff.domain.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

@DisplayName("VendorFieldLimits")
class VendorFieldLimitsTest {

    @Test
    @DisplayName("should truncate value exceeding max length")
    void shouldTruncate() {
        String long200 = "A".repeat(250);
        assertEquals(200,
                VendorFieldLimits.truncate(
                        long200,
                        VendorFieldLimits.MAX_NAME)
                        .length());
    }

    @Test
    @DisplayName("should return value within limit unchanged")
    void shouldReturnUnchanged() {
        assertEquals("Hello",
                VendorFieldLimits.truncate("Hello",
                        VendorFieldLimits.MAX_NAME));
    }

    @Test
    @DisplayName("should return null for null input")
    void shouldReturnNull() {
        assertNull(VendorFieldLimits.truncate(
                null, VendorFieldLimits.MAX_NAME));
    }
}
