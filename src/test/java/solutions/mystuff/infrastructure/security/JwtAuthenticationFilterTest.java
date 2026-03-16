package solutions.mystuff.infrastructure.security;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockFilterChain;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.core.context
        .SecurityContextHolder;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

@DisplayName("JWT Authentication Filter")
class JwtAuthenticationFilterTest {

    private static final String SECRET =
            "test-secret-key-that-is-at-least-32-bytes!!";

    private JwtTokenService tokenService;
    private JwtAuthenticationFilter filter;

    @BeforeEach
    void setUp() {
        tokenService = new JwtTokenService(
                new JwtProperties(SECRET, 3600));
        filter = new JwtAuthenticationFilter(tokenService);
        SecurityContextHolder.clearContext();
    }

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    @Test
    @DisplayName("should authenticate when valid token provided")
    void shouldAuthenticateWhenValidToken()
            throws Exception {
        String token = tokenService.generateToken("alice");
        MockHttpServletRequest request =
                new MockHttpServletRequest();
        request.addHeader("Authorization",
                "Bearer " + token);
        filter.doFilter(request,
                new MockHttpServletResponse(),
                new MockFilterChain());
        var auth = SecurityContextHolder.getContext()
                .getAuthentication();
        assertNotNull(auth);
        assertEquals("alice", auth.getPrincipal());
    }

    @Test
    @DisplayName("should skip auth when no header present")
    void shouldSkipWhenNoHeader() throws Exception {
        filter.doFilter(new MockHttpServletRequest(),
                new MockHttpServletResponse(),
                new MockFilterChain());
        assertNull(SecurityContextHolder.getContext()
                .getAuthentication());
    }

    @Test
    @DisplayName("should skip auth when invalid token provided")
    void shouldSkipWhenInvalidToken() throws Exception {
        MockHttpServletRequest request =
                new MockHttpServletRequest();
        request.addHeader("Authorization",
                "Bearer invalid.token.here");
        filter.doFilter(request,
                new MockHttpServletResponse(),
                new MockFilterChain());
        assertNull(SecurityContextHolder.getContext()
                .getAuthentication());
    }

    @Test
    @DisplayName("should skip auth when non-Bearer header")
    void shouldSkipWhenNonBearerHeader() throws Exception {
        MockHttpServletRequest request =
                new MockHttpServletRequest();
        request.addHeader("Authorization",
                "Basic dXNlcjpwYXNz");
        filter.doFilter(request,
                new MockHttpServletResponse(),
                new MockFilterChain());
        assertNull(SecurityContextHolder.getContext()
                .getAuthentication());
    }
}
