package solutions.mystuff.infrastructure.security;

import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.Instant;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.JwsHeader;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.security.oauth2.jwt.JwtException;
import org.springframework.security.oauth2.jwt.JwtTimestampValidator;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtEncoder;
import org.springframework.stereotype.Component;

import com.nimbusds.jose.jwk.source.ImmutableSecret;

/**
 * Generates and validates HMAC-SHA256 signed JWT tokens.
 *
 * <div class="mermaid">
 * sequenceDiagram
 *     participant C as Client
 *     participant S as JwtTokenService
 *     C->>S: generateToken(username)
 *     S-->>C: signed JWT string
 *     C->>S: validateToken(token)
 *     S-->>C: username or throws
 * </div>
 *
 * @see JwtProperties
 * @see JwtAuthenticationFilter
 */
@Component
public class JwtTokenService {

    private final NimbusJwtEncoder encoder;
    private final NimbusJwtDecoder decoder;
    private final long expiration;

    /** Creates a token service with the given JWT properties. */
    public JwtTokenService(JwtProperties properties) {
        SecretKey key = new SecretKeySpec(
                properties.secret().getBytes(
                        StandardCharsets.UTF_8),
                "HmacSHA256");
        this.encoder = new NimbusJwtEncoder(
                new ImmutableSecret<>(key));
        NimbusJwtDecoder built = NimbusJwtDecoder
                .withSecretKey(key)
                .macAlgorithm(MacAlgorithm.HS256)
                .build();
        built.setJwtValidator(
                new JwtTimestampValidator(
                        Duration.ZERO));
        this.decoder = built;
        this.expiration = properties.expiration();
    }

    /** Generates a signed JWT for the given username. */
    public String generateToken(String username) {
        Instant now = Instant.now();
        JwtClaimsSet claims = JwtClaimsSet.builder()
                .subject(username)
                .issuedAt(now)
                .expiresAt(now.plusSeconds(expiration))
                .build();
        JwsHeader header = JwsHeader
                .with(MacAlgorithm.HS256).build();
        return encoder.encode(
                JwtEncoderParameters.from(header, claims))
                .getTokenValue();
    }

    /**
     * Validates the token and returns the subject.
     *
     * @throws JwtException if the token is invalid or expired
     */
    public String validateToken(String token) {
        Jwt jwt = decoder.decode(token);
        return jwt.getSubject();
    }
}
