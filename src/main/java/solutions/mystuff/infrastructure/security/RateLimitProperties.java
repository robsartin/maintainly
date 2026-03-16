package solutions.mystuff.infrastructure.security;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Configuration properties for API rate limiting.
 *
 * <div class="mermaid">
 * classDiagram
 *     class RateLimitProperties {
 *         int requestsPerSecond
 *         int loginRequestsPerSecond
 *     }
 * </div>
 *
 * @see RateLimitFilter
 */
@ConfigurationProperties(prefix = "app.rate-limit")
public record RateLimitProperties(
        int requestsPerSecond,
        int loginRequestsPerSecond) {
}
