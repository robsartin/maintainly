package solutions.mystuff.application.web.api;

/**
 * Response body containing a JWT access token.
 *
 * <div class="mermaid">
 * classDiagram
 *     class TokenResponse {
 *         String token
 *     }
 * </div>
 *
 * @see AuthController
 */
public record TokenResponse(String token) {
}
