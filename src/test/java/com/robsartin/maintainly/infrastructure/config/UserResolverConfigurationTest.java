package com.robsartin.maintainly.infrastructure.config;

import java.util.Optional;
import java.util.UUID;

import com.robsartin.maintainly.domain.model.AppUser;
import com.robsartin.maintainly.domain.port.out.AppUserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@DisplayName("UserResolverConfiguration")
class UserResolverConfigurationTest {

    private final AppUserRepository userRepo =
            mock(AppUserRepository.class);
    private final UserResolverConfiguration resolver =
            new UserResolverConfiguration(userRepo);

    @Test
    @DisplayName("should return existing user")
    void shouldReturnExistingUser() {
        AppUser existing = new AppUser(
                UUID.randomUUID(), "alice");
        when(userRepo.findByUsername("alice"))
                .thenReturn(Optional.of(existing));
        AppUser result = resolver.resolveOrCreate("alice");
        assertEquals(existing, result);
        verify(userRepo, never()).save(any());
    }

    @Test
    @DisplayName("should create new user when not found")
    void shouldCreateNewUser() {
        AppUser saved = new AppUser(
                UUID.randomUUID(), "newuser");
        when(userRepo.findByUsername("newuser"))
                .thenReturn(Optional.empty());
        when(userRepo.save(any(AppUser.class)))
                .thenReturn(saved);
        AppUser result =
                resolver.resolveOrCreate("newuser");
        assertEquals(saved, result);
        verify(userRepo).save(any(AppUser.class));
    }
}
