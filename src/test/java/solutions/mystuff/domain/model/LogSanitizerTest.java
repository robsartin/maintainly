package solutions.mystuff.domain.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

@DisplayName("LogSanitizer")
class LogSanitizerTest {

    @Test
    @DisplayName("should return null for null input")
    void shouldReturnNullForNull() {
        assertNull(LogSanitizer.sanitize(null));
    }

    @Test
    @DisplayName("should pass through clean input")
    void shouldPassThroughClean() {
        assertEquals("hello world",
                LogSanitizer.sanitize("hello world"));
    }

    @Test
    @DisplayName("should replace newlines with underscores")
    void shouldReplaceNewlines() {
        assertEquals("line1_line2",
                LogSanitizer.sanitize("line1\nline2"));
    }

    @Test
    @DisplayName("should replace carriage returns with underscores")
    void shouldReplaceCarriageReturns() {
        assertEquals("line1_line2",
                LogSanitizer.sanitize("line1\rline2"));
    }

    @Test
    @DisplayName("should replace tabs with underscores")
    void shouldReplaceTabs() {
        assertEquals("col1_col2",
                LogSanitizer.sanitize("col1\tcol2"));
    }

    @Test
    @DisplayName("should strip other control characters")
    void shouldStripControlChars() {
        assertEquals("ab",
                LogSanitizer.sanitize("a\u0001b"));
    }

    @Test
    @DisplayName("should handle CRLF injection attack")
    void shouldHandleCrlfInjection() {
        String attack = "user\r\nINFO  fake.Logger"
                + " - Fake log entry";
        String sanitized = LogSanitizer.sanitize(attack);
        assertEquals("user__INFO  fake.Logger"
                + " - Fake log entry", sanitized);
    }

    @Test
    @DisplayName("should return empty for empty input")
    void shouldReturnEmptyForEmpty() {
        assertEquals("", LogSanitizer.sanitize(""));
    }
}
