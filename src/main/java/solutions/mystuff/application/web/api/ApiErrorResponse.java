package solutions.mystuff.application.web.api;

import java.time.Instant;

/**
 * Standard error response body for REST API endpoints.
 *
 * <div class="mermaid">
 * classDiagram
 *     class ApiErrorResponse {
 *         int status
 *         String error
 *         String message
 *         Instant timestamp
 *     }
 * </div>
 *
 * @see ApiErrorAdvice
 */
public record ApiErrorResponse(
        int status,
        String error,
        String message,
        Instant timestamp) {
}
