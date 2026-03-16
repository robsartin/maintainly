package solutions.mystuff.infrastructure.security;

/**
 * Published when a rate limit is exceeded for an IP address.
 *
 * <div class="mermaid">
 * classDiagram
 *     class RateLimitExceededEvent {
 *         String ipAddress
 *     }
 * </div>
 *
 * @see RateLimitFilter
 */
public record RateLimitExceededEvent(String ipAddress) {
}
