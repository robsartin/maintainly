package com.robsartin.maintainly.infrastructure.config;

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

@Configuration
@EnableWebSecurity
public class SecurityConfiguration {

    private static final Logger log =
            LoggerFactory.getLogger(SecurityConfiguration.class);

    @Bean
    @Profile("prod")
    public SecurityFilterChain prodSecurityFilterChain(
            HttpSecurity http) throws Exception {
        log.info("Configuring production security "
                + "with Google OAuth2");
        http
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/actuator/health")
                    .permitAll()
                .anyRequest().authenticated()
            )
            .oauth2Login(oauth2 -> oauth2
                .defaultSuccessUrl("/", true)
            )
            .csrf(csrf -> csrf
                .ignoringRequestMatchers("/actuator/**")
            );
        return http.build();
    }

    @Bean
    @Profile("!prod")
    public SecurityFilterChain devSecurityFilterChain(
            HttpSecurity http) throws Exception {
        log.info("Configuring development security "
                + "with form login (dev/dev)");
        http
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/actuator/health")
                    .permitAll()
                .anyRequest().authenticated()
            )
            .formLogin(form -> form
                .defaultSuccessUrl("/", true)
                .permitAll()
            )
            .csrf(csrf -> csrf
                .ignoringRequestMatchers("/actuator/**")
            );
        return http.build();
    }

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

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
