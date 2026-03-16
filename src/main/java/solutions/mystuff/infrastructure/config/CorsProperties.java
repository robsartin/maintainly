package solutions.mystuff.infrastructure.config;

import java.util.List;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Configuration properties for CORS allowed origins.
 *
 * <div class="mermaid">
 * classDiagram
 *     class CorsProperties {
 *         List~String~ allowedOrigins
 *     }
 * </div>
 *
 * @see ApiSecurityConfiguration
 */
@ConfigurationProperties(prefix = "app.cors")
public record CorsProperties(List<String> allowedOrigins) {
}
