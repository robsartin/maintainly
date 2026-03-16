package solutions.mystuff.infrastructure.security;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.mock.web.MockFilterChain;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;

@DisplayName("Rate Limit Filter")
class RateLimitFilterTest {

    private RateLimitFilter filter;
    private ApplicationEventPublisher publisher;

    @BeforeEach
    void setUp() {
        publisher = mock(ApplicationEventPublisher.class);
        filter = new RateLimitFilter(
                new RateLimitProperties(5, 2),
                publisher);
    }

    @Test
    @DisplayName("should allow requests under limit")
    void shouldAllowUnderLimit() {
        assertTrue(filter.tryConsume("10.0.0.1", 5));
        assertTrue(filter.tryConsume("10.0.0.1", 5));
    }

    @Test
    @DisplayName("should reject requests over limit")
    void shouldRejectOverLimit() {
        for (int i = 0; i < 5; i++) {
            filter.tryConsume("10.0.0.2", 5);
        }
        assertFalse(filter.tryConsume("10.0.0.2", 5));
    }

    @Test
    @DisplayName("should apply login limit to auth endpoint")
    void shouldApplyLoginLimit() throws Exception {
        MockHttpServletRequest request =
                new MockHttpServletRequest(
                        "POST", "/api/auth/token");
        request.setRemoteAddr("10.0.0.3");
        for (int i = 0; i < 2; i++) {
            MockHttpServletResponse response =
                    new MockHttpServletResponse();
            filter.doFilter(request, response,
                    new MockFilterChain());
            assertEquals(200, response.getStatus());
        }
        MockHttpServletResponse blocked =
                new MockHttpServletResponse();
        filter.doFilter(request, blocked,
                new MockFilterChain());
        assertEquals(429, blocked.getStatus());
    }

    @Test
    @DisplayName("should isolate limits per IP address")
    void shouldIsolateLimitsPerIp() {
        for (int i = 0; i < 5; i++) {
            filter.tryConsume("10.0.0.4", 5);
        }
        assertTrue(filter.tryConsume("10.0.0.5", 5));
    }

    @Test
    @DisplayName("should clean up stale entries")
    void shouldCleanUpStaleEntries() {
        filter.tryConsume("10.0.0.6", 5);
        filter.cleanup(0);
        assertTrue(filter.tryConsume("10.0.0.6", 5));
    }
}
