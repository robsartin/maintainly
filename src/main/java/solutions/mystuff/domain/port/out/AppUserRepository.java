package solutions.mystuff.domain.port.out;

import java.util.Optional;

import solutions.mystuff.domain.model.AppUser;

public interface AppUserRepository {

    Optional<AppUser> findByUsername(String username);

    AppUser save(AppUser user);
}
