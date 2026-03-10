package com.robsartin.maintainly.infrastructure.config;

import com.robsartin.maintainly.domain.model.AppUser;
import com.robsartin.maintainly.domain.model.UuidV7;
import com.robsartin.maintainly.domain.port.in.UserResolver;
import com.robsartin.maintainly.domain.port.out.AppUserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class UserResolverConfiguration implements UserResolver {

    private static final Logger log =
            LoggerFactory.getLogger(
                    UserResolverConfiguration.class);

    private final AppUserRepository appUserRepository;

    public UserResolverConfiguration(
            AppUserRepository appUserRepository) {
        this.appUserRepository = appUserRepository;
    }

    public AppUser resolveOrCreate(String username) {
        return appUserRepository.findByUsername(username)
                .orElseGet(() -> {
                    log.info("Creating new user: {}",
                            username);
                    AppUser u = new AppUser(
                            UuidV7.generate(), username);
                    return appUserRepository.save(u);
                });
    }
}
