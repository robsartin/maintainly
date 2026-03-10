package com.robsartin.maintainly.infrastructure.config;

import java.util.UUID;

import com.robsartin.maintainly.domain.model.AppUser;
import com.robsartin.maintainly.domain.port.in.UserResolver;
import com.robsartin.maintainly.domain.port.out.AppUserRepository;
import org.springframework.stereotype.Component;

@Component
public class UserResolverConfiguration implements UserResolver {

    private final AppUserRepository appUserRepository;

    public UserResolverConfiguration(
            AppUserRepository appUserRepository) {
        this.appUserRepository = appUserRepository;
    }

    public AppUser resolveOrCreate(String username) {
        return appUserRepository.findByUsername(username)
                .orElseGet(() -> {
                    AppUser u = new AppUser(
                            UUID.randomUUID(), username);
                    return appUserRepository.save(u);
                });
    }
}
