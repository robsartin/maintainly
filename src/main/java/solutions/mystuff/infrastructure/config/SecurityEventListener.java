package solutions.mystuff.infrastructure.config;

import solutions.mystuff.domain.model.LogSanitizer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.event.EventListener;
import org.springframework.security.authentication.event
        .AbstractAuthenticationFailureEvent;
import org.springframework.security.authentication.event
        .AuthenticationSuccessEvent;
import org.springframework.stereotype.Component;

/**
 * Logs authentication success and failure events for security auditing.
 *
 * <div class="mermaid">
 * flowchart LR
 *     A[Spring Security] -->|success| B[log.info]
 *     A -->|failure| C[log.warn]
 * </div>
 *
 * @see SecurityConfiguration
 */
@Component
public class SecurityEventListener {

    private static final Logger log =
            LoggerFactory.getLogger(
                    SecurityEventListener.class);

    /** Logs successful authentication events. */
    @EventListener
    public void onSuccess(
            AuthenticationSuccessEvent event) {
        String name = LogSanitizer.sanitize(
                event.getAuthentication().getName());
        log.info("Authentication success: user={}",
                name);
    }

    /** Logs failed authentication events. */
    @EventListener
    public void onFailure(
            AbstractAuthenticationFailureEvent event) {
        String name = LogSanitizer.sanitize(
                event.getAuthentication().getName());
        log.warn("Authentication failure: user={}, "
                + "reason={}", name,
                event.getException().getMessage());
    }
}
