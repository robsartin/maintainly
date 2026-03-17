package solutions.mystuff.application.web.api;

import solutions.mystuff.domain.port.in.ApiTokenService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Issues JWT tokens in exchange for valid credentials.
 *
 * <div class="mermaid">
 * sequenceDiagram
 *     participant C as Client
 *     participant A as AuthController
 *     participant S as ApiTokenService
 *     C->>A: POST /api/auth/token
 *     A->>S: issueToken(username, password)
 *     S-->>A: JWT string
 *     A-->>C: TokenResponse
 * </div>
 *
 * @see ApiTokenService
 * @see TokenRequest
 * @see TokenResponse
 */
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final ApiTokenService tokenService;

    /** Creates an auth controller with the token service. */
    public AuthController(ApiTokenService tokenService) {
        this.tokenService = tokenService;
    }

    /** Authenticates credentials and returns a signed JWT. */
    @PostMapping("/token")
    public TokenResponse token(
            @RequestBody TokenRequest request) {
        String token = tokenService.issueToken(
                request.username(), request.password());
        return new TokenResponse(token);
    }
}
