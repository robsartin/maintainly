package solutions.mystuff.infrastructure.security;

import java.io.IOException;
import java.util.List;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication
        .UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority
        .SimpleGrantedAuthority;
import org.springframework.security.core.context
        .SecurityContextHolder;
import org.springframework.security.oauth2.jwt.JwtException;
import org.springframework.web.filter.OncePerRequestFilter;

/**
 * Extracts a Bearer JWT from the Authorization header and sets
 * the Spring Security context when the token is valid.
 *
 * <div class="mermaid">
 * sequenceDiagram
 *     participant R as Request
 *     participant F as JwtAuthFilter
 *     participant S as JwtTokenService
 *     participant C as SecurityContext
 *     R->>F: Authorization: Bearer &lt;token&gt;
 *     F->>S: validateToken(token)
 *     S-->>F: username
 *     F->>C: setAuthentication
 * </div>
 *
 * @see JwtTokenService
 */
public class JwtAuthenticationFilter
        extends OncePerRequestFilter {

    private static final Logger log =
            LoggerFactory.getLogger(
                    JwtAuthenticationFilter.class);
    private static final String BEARER_PREFIX = "Bearer ";

    private final JwtTokenService tokenService;

    /** Creates a filter backed by the given token service. */
    public JwtAuthenticationFilter(
            JwtTokenService tokenService) {
        this.tokenService = tokenService;
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain chain)
            throws ServletException, IOException {
        String header = request.getHeader("Authorization");
        if (header != null
                && header.startsWith(BEARER_PREFIX)) {
            authenticateFromToken(
                    header.substring(BEARER_PREFIX.length()));
        }
        chain.doFilter(request, response);
    }

    private void authenticateFromToken(String token) {
        try {
            String username =
                    tokenService.validateToken(token);
            var authorities = List.of(
                    new SimpleGrantedAuthority("ROLE_USER"));
            var auth =
                    new UsernamePasswordAuthenticationToken(
                            username, null, authorities);
            SecurityContextHolder.getContext()
                    .setAuthentication(auth);
        } catch (JwtException ex) {
            log.debug("Invalid JWT token: {}",
                    ex.getMessage());
        }
    }
}
