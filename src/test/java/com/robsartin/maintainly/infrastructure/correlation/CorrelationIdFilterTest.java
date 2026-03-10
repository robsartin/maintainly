package com.robsartin.maintainly.infrastructure.correlation;

import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
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
    @DisplayName("should use existing header value")
    void shouldUseExistingHeader() throws Exception {
        HttpServletRequest req =
                mock(HttpServletRequest.class);
        HttpServletResponse res =
                mock(HttpServletResponse.class);
        FilterChain chain = mock(FilterChain.class);
        when(req.getHeader("X-Correlation-Id"))
                .thenReturn("existing-id");
        filter.doFilterInternal(req, res, chain);
        verify(res).setHeader(
                eq("X-Correlation-Id"), eq("existing-id"));
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
        HttpServletRequest req =
                mock(HttpServletRequest.class);
        HttpServletResponse res =
                mock(HttpServletResponse.class);
        FilterChain chain = mock(FilterChain.class);
        when(req.getHeader("X-Correlation-Id"))
                .thenReturn("temp-id");
        filter.doFilterInternal(req, res, chain);
        assertNull(CorrelationIdContext.getId());
    }
}
