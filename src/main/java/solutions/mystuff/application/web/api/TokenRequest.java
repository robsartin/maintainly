package solutions.mystuff.application.web.api;

/**
 * Request body for the token endpoint.
 *
 * <div class="mermaid">
 * classDiagram
 *     class TokenRequest {
 *         String username
 *         String password
 *     }
 * </div>
 *
 * @see AuthController
 */
public record TokenRequest(
        String username, String password) {
}
