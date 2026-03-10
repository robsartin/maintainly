package solutions.mystuff.application.web;

import java.security.Principal;
import java.util.Map;
import java.util.Optional;

import solutions.mystuff.domain.model.AppUser;
import solutions.mystuff.domain.model.UuidV7;
import solutions.mystuff.domain.port.in.UserResolver;
import solutions.mystuff.domain.port.out
        .ServiceRecordRepository;
import solutions.mystuff.domain.port.out
        .ServiceScheduleRepository;
import solutions.mystuff.domain.port.out
        .VendorRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.security.oauth2.client
        .authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user
        .DefaultOAuth2User;
import org.springframework.security.oauth2.core.user
        .OAuth2User;
import org.springframework.security.core.authority
        .SimpleGrantedAuthority;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@DisplayName("ControllerHelper")
class ControllerHelperTest {

    private final UserResolver userResolver =
            mock(UserResolver.class);
    private final ServiceRecordRepository recordRepo =
            mock(ServiceRecordRepository.class);
    private final ServiceScheduleRepository schedRepo =
            mock(ServiceScheduleRepository.class);
    private final VendorRepository vendorRepo =
            mock(VendorRepository.class);
    private final ControllerHelper helper =
            new ControllerHelper(
                    userResolver, recordRepo,
                    schedRepo, vendorRepo);

    @Test
    @DisplayName("should extract email from OAuth2 token")
    void shouldExtractEmailFromOauth2() {
        AppUser user = new AppUser(
                UuidV7.generate(), "alice@example.com");
        when(userResolver.resolveOrCreate(
                "alice@example.com")).thenReturn(user);

        OAuth2User oauthUser = new DefaultOAuth2User(
                List.of(new SimpleGrantedAuthority(
                        "ROLE_USER")),
                Map.of("sub", "12345",
                        "email", "alice@example.com",
                        "name", "Alice"),
                "sub");
        OAuth2AuthenticationToken token =
                new OAuth2AuthenticationToken(
                        oauthUser, oauthUser.getAuthorities(),
                        "google");

        AppUser result = helper.resolveUser(token);

        assertEquals(user, result);
        verify(userResolver)
                .resolveOrCreate("alice@example.com");
    }

    @Test
    @DisplayName("should fall back to principal name "
            + "for non-OAuth2")
    void shouldFallBackToPrincipalName() {
        AppUser user = new AppUser(
                UuidV7.generate(), "dev");
        when(userResolver.resolveOrCreate("dev"))
                .thenReturn(user);

        Principal principal = () -> "dev";

        AppUser result = helper.resolveUser(principal);

        assertEquals(user, result);
        verify(userResolver).resolveOrCreate("dev");
    }

    @Test
    @DisplayName("should fall back to sub when OAuth2 "
            + "has no email")
    void shouldFallBackToSubWhenNoEmail() {
        AppUser user = new AppUser(
                UuidV7.generate(), "12345");
        when(userResolver.resolveOrCreate("12345"))
                .thenReturn(user);

        OAuth2User oauthUser = new DefaultOAuth2User(
                List.of(new SimpleGrantedAuthority(
                        "ROLE_USER")),
                Map.of("sub", "12345",
                        "name", "Alice"),
                "sub");
        OAuth2AuthenticationToken token =
                new OAuth2AuthenticationToken(
                        oauthUser, oauthUser.getAuthorities(),
                        "google");

        AppUser result = helper.resolveUser(token);

        assertEquals(user, result);
        verify(userResolver).resolveOrCreate("12345");
    }
}
