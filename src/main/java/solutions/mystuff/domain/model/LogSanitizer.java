package solutions.mystuff.domain.model;

/**
 * Strips control characters from strings before they are logged.
 *
 * <p>Prevents log injection attacks where an attacker embeds newlines
 * or ANSI escape sequences in user input to forge log entries.
 *
 * @see solutions.mystuff.infrastructure.correlation.CorrelationIdFilter
 */
public final class LogSanitizer {

    private LogSanitizer() {
    }

    /** Replaces newlines and tabs with underscores and strips other control characters. */
    public static String sanitize(String input) {
        if (input == null) {
            return null;
        }
        return input
                .replaceAll("[\\r\\n\\t]", "_")
                .replaceAll("[\\x00-\\x1F\\x7F]", "");
    }
}
