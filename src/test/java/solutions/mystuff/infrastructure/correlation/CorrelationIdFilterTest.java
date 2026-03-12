package solutions.mystuff.infrastructure.correlation;

import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import org.mockito.ArgumentCaptor;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@DisplayName("CorrelationIdFilter")
class CorrelationIdFilterTest {

    private final CorrelationIdFilter filter =
            new CorrelationIdFilter();

    @AfterEach
    void cleanup() {
        CorrelationIdContext.clear();
    }

    @Test
    @DisplayName("should use valid UUID header value")
    void shouldUseValidUuidHeader() throws Exception {
        String validUuid =
                "550e8400-e29b-41d4-a716-446655440000";
        HttpServletRequest req =
                mock(HttpServletRequest.class);
        HttpServletResponse res =
                mock(HttpServletResponse.class);
        FilterChain chain = mock(FilterChain.class);
        when(req.getHeader("X-Correlation-Id"))
                .thenReturn(validUuid);
        filter.doFilterInternal(req, res, chain);
        verify(res).setHeader(
                eq("X-Correlation-Id"), eq(validUuid));
        verify(chain).doFilter(req, res);
    }

    @Test
    @DisplayName("should generate UUID V7 when no header")
    void shouldGenerateWhenNoHeader() throws Exception {
        HttpServletRequest req =
                mock(HttpServletRequest.class);
        HttpServletResponse res =
                mock(HttpServletResponse.class);
        FilterChain chain = mock(FilterChain.class);
        when(req.getHeader("X-Correlation-Id"))
                .thenReturn(null);
        filter.doFilterInternal(req, res, chain);
        verify(chain).doFilter(req, res);
        assertNull(CorrelationIdContext.getId());
    }

    @Test
    @DisplayName("should clear context after request")
    void shouldClearAfterRequest() throws Exception {
        String validUuid =
                "550e8400-e29b-41d4-a716-446655440000";
        HttpServletRequest req =
                mock(HttpServletRequest.class);
        HttpServletResponse res =
                mock(HttpServletResponse.class);
        FilterChain chain = mock(FilterChain.class);
        when(req.getHeader("X-Correlation-Id"))
                .thenReturn(validUuid);
        filter.doFilterInternal(req, res, chain);
        assertNull(CorrelationIdContext.getId());
    }

    @Test
    @DisplayName("should reject non-UUID correlation ID")
    void shouldRejectNonUuid() throws Exception {
        HttpServletRequest req =
                mock(HttpServletRequest.class);
        HttpServletResponse res =
                mock(HttpServletResponse.class);
        FilterChain chain = mock(FilterChain.class);
        when(req.getHeader("X-Correlation-Id"))
                .thenReturn(
                        "malicious\nINFO - fake log");
        ArgumentCaptor<String> captor =
                ArgumentCaptor.forClass(String.class);
        filter.doFilterInternal(req, res, chain);
        verify(res).setHeader(
                eq("X-Correlation-Id"),
                captor.capture());
        String actual = captor.getValue();
        assertFalse(actual.contains("\n"),
                "Must not contain newline");
        assertTrue(actual.matches(
                "[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-"
                + "[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-"
                + "[0-9a-fA-F]{12}"),
                "Should be a valid UUID");
    }

    @Test
    @DisplayName("should reject empty correlation ID")
    void shouldRejectEmpty() throws Exception {
        HttpServletRequest req =
                mock(HttpServletRequest.class);
        HttpServletResponse res =
                mock(HttpServletResponse.class);
        FilterChain chain = mock(FilterChain.class);
        when(req.getHeader("X-Correlation-Id"))
                .thenReturn("");
        ArgumentCaptor<String> captor =
                ArgumentCaptor.forClass(String.class);
        filter.doFilterInternal(req, res, chain);
        verify(res).setHeader(
                eq("X-Correlation-Id"),
                captor.capture());
        assertTrue(captor.getValue().matches(
                "[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-"
                + "[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-"
                + "[0-9a-fA-F]{12}"),
                "Should generate a valid UUID");
    }
}
