package solutions.mystuff.domain.port.out;

import java.util.Optional;
import java.util.UUID;

import solutions.mystuff.domain.model.AppUser;

public interface AppUserRepository {

    Optional<AppUser> findByUsername(String username);

    Optional<AppUser> findById(UUID id);

    AppUser save(AppUser user);
}
