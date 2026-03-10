package solutions.mystuff.infrastructure.config;

import solutions.mystuff.domain.model.AppUser;
import solutions.mystuff.domain.model.UuidV7;
import solutions.mystuff.domain.port.in.UserResolver;
import solutions.mystuff.domain.port.out.AppUserRepository;
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
