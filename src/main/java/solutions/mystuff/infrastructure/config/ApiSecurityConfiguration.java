package solutions.mystuff.infrastructure.config;

import java.util.List;

import solutions.mystuff.infrastructure.security.JwtAuthenticationFilter;
import solutions.mystuff.infrastructure.security.JwtProperties;
import solutions.mystuff.infrastructure.security.JwtTokenService;
import solutions.mystuff.infrastructure.security.RateLimitFilter;
import solutions.mystuff.infrastructure.security.RateLimitProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.HttpStatusEntryPoint;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

/**
 * Security filter chain for the {@code /api/**} REST endpoints.
 *
 * <p>Configures stateless JWT authentication, CORS, rate limiting,
 * and disables CSRF for the API path prefix. The token endpoint
 * at {@code /api/auth/token} is publicly accessible.
 *
 * <div class="mermaid">
 * flowchart TD
 *     A[/api/** request] --> B[RateLimitFilter]
 *     B --> C[JwtAuthenticationFilter]
 *     C --> D{Authenticated?}
 *     D -->|yes| E[Controller]
 *     D -->|no /auth/token| E
 *     D -->|no other| F[401 Unauthorized]
 * </div>
 *
 * @see SecurityConfiguration
 * @see JwtAuthenticationFilter
 * @see RateLimitFilter
 */
@Configuration
@EnableConfigurationProperties({
    JwtProperties.class,
    RateLimitProperties.class,
    CorsProperties.class
})
public class ApiSecurityConfiguration {

    /** Creates the API security filter chain with JWT and rate limiting. */
    @Bean
    @Order(1)
    public SecurityFilterChain apiFilterChain(
            HttpSecurity http,
            JwtTokenService tokenService,
            RateLimitProperties rateLimitProps,
            CorsProperties corsProps,
            ApplicationEventPublisher publisher)
            throws Exception {
        var jwtFilter =
                new JwtAuthenticationFilter(tokenService);
        var rateLimitFilter =
                new RateLimitFilter(
                        rateLimitProps, publisher);
        http.securityMatcher("/api/**")
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/api/auth/token")
                        .permitAll()
                .anyRequest().authenticated())
            .csrf(csrf -> csrf.disable())
            .sessionManagement(sm -> sm
                .sessionCreationPolicy(
                        SessionCreationPolicy.STATELESS))
            .exceptionHandling(ex -> ex
                .authenticationEntryPoint(
                        new HttpStatusEntryPoint(
                                HttpStatus.UNAUTHORIZED)))
            .cors(cors -> cors.configurationSource(
                    corsSource(corsProps)))
            .addFilterBefore(rateLimitFilter,
                    UsernamePasswordAuthenticationFilter
                            .class)
            .addFilterBefore(jwtFilter,
                    UsernamePasswordAuthenticationFilter
                            .class);
        return http.build();
    }

    private CorsConfigurationSource corsSource(
            CorsProperties props) {
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowedOrigins(props.allowedOrigins());
        config.setAllowedMethods(
                List.of("GET", "POST", "PUT", "DELETE"));
        config.setAllowedHeaders(
                List.of("Authorization", "Content-Type"));
        var source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/api/**", config);
        return source;
    }
}
