package solutions.mystuff.infrastructure.security;

/**
 * Published when a JWT token is issued to a user.
 *
 * <div class="mermaid">
 * classDiagram
 *     class JwtTokenIssuedEvent {
 *         String username
 *     }
 * </div>
 *
 * @see JwtTokenService
 */
public record JwtTokenIssuedEvent(String username) {
}
