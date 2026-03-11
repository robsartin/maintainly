package solutions.mystuff.infrastructure.correlation;

import java.io.IOException;

import solutions.mystuff.domain.model.UuidV7;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

/**
 * Servlet filter that assigns a correlation ID to every request.
 *
 * <div class="mermaid">
 * sequenceDiagram
 *     participant C as Client
 *     participant F as CorrelationIdFilter
 *     participant S as FilterChain
 *     C->>F: HTTP request
 *     F->>F: generate or read X-Correlation-Id
 *     F->>F: set MDC and CorrelationIdContext
 *     F->>S: chain.doFilter
 *     S-->>F: response
 *     F->>C: add X-Correlation-Id header
 *     F->>F: clear MDC and context
 * </div>
 *
 * @see CorrelationIdContext
 * @see OncePerRequestFilter
 */
@Component
public class CorrelationIdFilter extends OncePerRequestFilter {

    private static final String HEADER = "X-Correlation-Id";
    private static final String MDC_KEY = "correlationId";

    /** {@inheritDoc} */
    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain)
            throws ServletException, IOException {
        String id = request.getHeader(HEADER);
        if (id == null || id.isBlank()) {
            id = UuidV7.generate().toString();
        }
        CorrelationIdContext.setId(id);
        MDC.put(MDC_KEY, id);
        response.setHeader(HEADER, id);
        try {
            filterChain.doFilter(request, response);
        } finally {
            CorrelationIdContext.clear();
            MDC.remove(MDC_KEY);
        }
    }
}
