package solutions.mystuff.infrastructure.security;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.security.oauth2.jwt.JwtException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

@DisplayName("JWT Token Service")
class JwtTokenServiceTest {

    private static final String SECRET =
            "test-secret-key-that-is-at-least-32-bytes!!";

    private JwtTokenService service;

    @BeforeEach
    void setUp() {
        service = new JwtTokenService(
                new JwtProperties(SECRET, 3600));
    }

    @Test
    @DisplayName("should generate a non-null token")
    void shouldGenerateToken() {
        String token = service.generateToken("alice");
        assertNotNull(token);
    }

    @Test
    @DisplayName("should validate and return subject")
    void shouldValidateAndReturnSubject() {
        String token = service.generateToken("bob");
        String subject = service.validateToken(token);
        assertEquals("bob", subject);
    }

    @Test
    @DisplayName("should reject expired token")
    void shouldRejectExpiredToken() throws Exception {
        JwtTokenService shortLived =
                new JwtTokenService(
                        new JwtProperties(SECRET, 1));
        String token = shortLived.generateToken("carol");
        Thread.sleep(1100);
        assertThrows(JwtException.class,
                () -> service.validateToken(token));
    }

    @Test
    @DisplayName("should reject tampered token")
    void shouldRejectTamperedToken() {
        String token = service.generateToken("dave");
        String tampered = token + "tampered";
        assertThrows(JwtException.class,
                () -> service.validateToken(tampered));
    }

    @Test
    @DisplayName("should reject token signed with different key")
    void shouldRejectWrongKeyToken() {
        JwtTokenService other = new JwtTokenService(
                new JwtProperties(
                        "different-secret-key-also-at-least-32bytes!",
                        3600));
        String token = other.generateToken("eve");
        assertThrows(JwtException.class,
                () -> service.validateToken(token));
    }
}
