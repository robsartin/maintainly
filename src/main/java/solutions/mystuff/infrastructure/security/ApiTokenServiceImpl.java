package solutions.mystuff.infrastructure.security;

import solutions.mystuff.domain.port.in.ApiTokenService;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication
        .UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation
        .authentication.configuration
        .AuthenticationConfiguration;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

/**
 * Authenticates credentials via Spring Security and issues a JWT.
 *
 * <div class="mermaid">
 * sequenceDiagram
 *     participant C as Caller
 *     participant S as ApiTokenServiceImpl
 *     participant AM as AuthenticationManager
 *     participant J as JwtTokenService
 *     C->>S: issueToken(user, pass)
 *     S->>AM: authenticate
 *     AM-->>S: Authentication
 *     S->>J: generateToken(username)
 *     J-->>S: JWT
 *     S-->>C: JWT
 * </div>
 *
 * @see ApiTokenService
 * @see JwtTokenService
 */
@Service
public class ApiTokenServiceImpl
        implements ApiTokenService {

    private final AuthenticationManager authManager;
    private final JwtTokenService tokenService;
    private final ApplicationEventPublisher publisher;

    /** Creates the service with its required dependencies. */
    public ApiTokenServiceImpl(
            AuthenticationConfiguration authConfig,
            JwtTokenService tokenService,
            ApplicationEventPublisher publisher)
            throws Exception {
        this.authManager =
                authConfig.getAuthenticationManager();
        this.tokenService = tokenService;
        this.publisher = publisher;
    }

    @Override
    public String issueToken(
            String username, String password) {
        Authentication auth = authManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        username, password));
        String token = tokenService.generateToken(
                auth.getName());
        publisher.publishEvent(
                new JwtTokenIssuedEvent(auth.getName()));
        return token;
    }
}
