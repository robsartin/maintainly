package solutions.mystuff.infrastructure.security;

import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.filter.OncePerRequestFilter;

/**
 * In-memory token-bucket rate limiter per client IP address.
 *
 * <p>Login endpoints receive a tighter limit than general API
 * requests. Publishes {@link RateLimitExceededEvent} when a
 * client exceeds the allowed rate.
 *
 * <div class="mermaid">
 * sequenceDiagram
 *     participant C as Client
 *     participant F as RateLimitFilter
 *     participant B as TokenBucket
 *     C->>F: request
 *     F->>B: tryConsume(ip, limit)
 *     alt tokens available
 *         B-->>F: true
 *         F->>C: continue
 *     else exceeded
 *         B-->>F: false
 *         F->>C: 429 Too Many Requests
 *     end
 * </div>
 *
 * @see RateLimitProperties
 * @see RateLimitExceededEvent
 */
public class RateLimitFilter extends OncePerRequestFilter {

    private static final String AUTH_PATH =
            "/api/auth/token";

    private final RateLimitProperties properties;
    private final ApplicationEventPublisher publisher;
    private final ConcurrentHashMap<String, long[]> buckets =
            new ConcurrentHashMap<>();

    /** Creates a rate limit filter with the given config. */
    public RateLimitFilter(
            RateLimitProperties properties,
            ApplicationEventPublisher publisher) {
        this.properties = properties;
        this.publisher = publisher;
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain chain)
            throws ServletException, IOException {
        String ip = request.getRemoteAddr();
        int limit = resolveLimit(request);
        if (!tryConsume(ip, limit)) {
            publisher.publishEvent(
                    new RateLimitExceededEvent(ip));
            rejectRequest(response);
            return;
        }
        chain.doFilter(request, response);
    }

    private int resolveLimit(HttpServletRequest request) {
        if (AUTH_PATH.equals(request.getRequestURI())) {
            return properties.loginRequestsPerSecond();
        }
        return properties.requestsPerSecond();
    }

    private void rejectRequest(
            HttpServletResponse response)
            throws IOException {
        response.setStatus(
                HttpStatus.TOO_MANY_REQUESTS.value());
        response.setContentType(
                MediaType.APPLICATION_JSON_VALUE);
        response.getWriter().write(
                "{\"error\":\"Rate limit exceeded\"}");
    }

    boolean tryConsume(String key, int limit) {
        long nowNanos = System.nanoTime();
        long[] state = buckets.compute(key, (k, v) -> {
            if (v == null) {
                return new long[]{limit - 1, nowNanos};
            }
            return refillAndConsume(v, nowNanos, limit);
        });
        return state[0] >= 0;
    }

    private long[] refillAndConsume(
            long[] bucket, long nowNanos, int limit) {
        long elapsed = nowNanos - bucket[1];
        long refill =
                elapsed * limit / 1_000_000_000L;
        if (refill > 0) {
            bucket[0] = Math.min(limit, bucket[0] + refill);
            bucket[1] = nowNanos;
        }
        bucket[0]--;
        return bucket;
    }

    /** Removes stale bucket entries older than the threshold. */
    void cleanup(long maxAgeNanos) {
        long now = System.nanoTime();
        buckets.entrySet().removeIf(e ->
                (now - e.getValue()[1]) > maxAgeNanos);
    }
}
