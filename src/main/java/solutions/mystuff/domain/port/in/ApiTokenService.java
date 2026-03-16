package solutions.mystuff.domain.port.in;

/**
 * Inbound port for authenticating credentials and issuing API tokens.
 *
 * @see solutions.mystuff.application.web.api.AuthController
 */
public interface ApiTokenService {

    /**
     * Authenticates the given credentials and returns a signed token.
     *
     * @param username the username
     * @param password the password
     * @return signed token string
     * @throws org.springframework.security.authentication
     *         .BadCredentialsException if credentials are invalid
     */
    String issueToken(String username, String password);
}
