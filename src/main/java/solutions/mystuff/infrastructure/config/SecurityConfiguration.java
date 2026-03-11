package solutions.mystuff.infrastructure.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

/**
 * Configures Spring Security for production and development profiles.
 *
 * <pre>{@code
 * flowchart TD
 *     A[Profile check] -->|prod| B[OAuth2 SecurityFilterChain]
 *     A -->|!prod| C[Form-login SecurityFilterChain]
 *     A -->|!prod| D[InMemory UserDetailsService]
 *     A --> E[BCrypt PasswordEncoder]
 * }</pre>
 *
 * @see SecurityFilterChain
 * @see PasswordEncoder
 */
@Configuration
@EnableWebSecurity
public class SecurityConfiguration {

    private static final Logger log =
            LoggerFactory.getLogger(
                    SecurityConfiguration.class);

    private static final String[] PUBLIC_PATHS = {
        "/actuator/health",
        "/actuator/prometheus",
        "/api-docs/**",
        "/swagger-ui/**",
        "/swagger-ui.html"
    };

    /** Creates the OAuth2-based security filter chain for production. */
    @Bean
    @Profile("prod")
    public SecurityFilterChain prodFilterChain(
            HttpSecurity http) throws Exception {
        log.info("Configuring production security "
                + "with Google OAuth2");
        configureCommon(http);
        http.oauth2Login(oauth2 -> oauth2
                .defaultSuccessUrl("/", true));
        return http.build();
    }

    /** Creates the form-login security filter chain for development. */
    @Bean
    @Profile("!prod")
    public SecurityFilterChain devFilterChain(
            HttpSecurity http) throws Exception {
        log.info("Configuring development security "
                + "with form login (dev/dev)");
        configureCommon(http);
        http.formLogin(form -> form
                .defaultSuccessUrl("/", true)
                .permitAll());
        return http.build();
    }

    /** Creates an in-memory user details service with a dev/dev account. */
    @Bean
    @Profile("!prod")
    public UserDetailsService devUserDetailsService(
            PasswordEncoder encoder) {
        var devUser = User.builder()
                .username("dev")
                .password(encoder.encode("dev"))
                .roles("USER")
                .build();
        return new InMemoryUserDetailsManager(devUser);
    }

    /** Creates a BCrypt password encoder bean. */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    private void configureCommon(HttpSecurity http)
            throws Exception {
        http
            .authorizeHttpRequests(auth -> auth
                .requestMatchers(PUBLIC_PATHS).permitAll()
                .anyRequest().authenticated())
            .csrf(csrf -> csrf
                .ignoringRequestMatchers(
                        "/actuator/**"));
    }
}
