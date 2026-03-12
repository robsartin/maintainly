package solutions.mystuff.infrastructure.config;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.security.authentication
        .UsernamePasswordAuthenticationToken;
import org.springframework.security.authentication.event
        .AuthenticationSuccessEvent;
import org.springframework.security.authentication.event
        .AbstractAuthenticationFailureEvent;
import org.springframework.security.core.Authentication;
import org.springframework.security.authentication
        .BadCredentialsException;

import static org.junit.jupiter.api.Assertions
        .assertDoesNotThrow;

@DisplayName("SecurityEventListener")
class SecurityEventListenerTest {

    private final SecurityEventListener listener =
            new SecurityEventListener();

    @Test
    @DisplayName("should log authentication success")
    void shouldLogSuccess() {
        Authentication auth =
                new UsernamePasswordAuthenticationToken(
                        "testuser", "password");
        AuthenticationSuccessEvent event =
                new AuthenticationSuccessEvent(auth);
        assertDoesNotThrow(
                () -> listener.onSuccess(event));
    }

    @Test
    @DisplayName("should log authentication failure")
    void shouldLogFailure() {
        Authentication auth =
                new UsernamePasswordAuthenticationToken(
                        "baduser", "wrong");
        BadCredentialsException cause =
                new BadCredentialsException("Bad creds");
        AbstractAuthenticationFailureEvent event =
                new TestFailureEvent(auth, cause);
        assertDoesNotThrow(
                () -> listener.onFailure(event));
    }

    @Test
    @DisplayName("should sanitize username with newlines in success")
    void shouldSanitizeSuccessUsername() {
        Authentication auth =
                new UsernamePasswordAuthenticationToken(
                        "user\nINJECT", "password");
        AuthenticationSuccessEvent event =
                new AuthenticationSuccessEvent(auth);
        assertDoesNotThrow(
                () -> listener.onSuccess(event));
    }

    @Test
    @DisplayName("should sanitize username with newlines in failure")
    void shouldSanitizeFailureUsername() {
        Authentication auth =
                new UsernamePasswordAuthenticationToken(
                        "user\r\nINJECT", "password");
        BadCredentialsException cause =
                new BadCredentialsException("Bad creds");
        AbstractAuthenticationFailureEvent event =
                new TestFailureEvent(auth, cause);
        assertDoesNotThrow(
                () -> listener.onFailure(event));
    }

    private static class TestFailureEvent
            extends AbstractAuthenticationFailureEvent {
        TestFailureEvent(
                Authentication auth,
                org.springframework.security.core
                        .AuthenticationException ex) {
            super(auth, ex);
        }
    }
}
