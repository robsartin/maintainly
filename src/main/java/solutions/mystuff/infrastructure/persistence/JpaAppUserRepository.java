package solutions.mystuff.infrastructure.persistence;

import java.util.Optional;
import java.util.UUID;

import solutions.mystuff.domain.model.AppUser;
import solutions.mystuff.domain.port.out.AppUserRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface JpaAppUserRepository
        extends JpaRepository<AppUser, UUID>,
        AppUserRepository {

    @Override
    Optional<AppUser> findByUsername(String username);
}
