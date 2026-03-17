package solutions.mystuff.infrastructure.security;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Configuration properties for JWT token generation and validation.
 *
 * <div class="mermaid">
 * classDiagram
 *     class JwtProperties {
 *         String secret
 *         long expiration
 *     }
 * </div>
 *
 * @see JwtTokenService
 */
@ConfigurationProperties(prefix = "app.jwt")
public record JwtProperties(String secret, long expiration) {
}
