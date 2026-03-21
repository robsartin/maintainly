package solutions.mystuff.infrastructure.persistence;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import solutions.mystuff.domain.model.AppUser;
import solutions.mystuff.domain.port.out.AppUserRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA adapter for the {@link AppUserRepository} port.
 *
 * <div class="mermaid">
 * classDiagram
 *     class JpaAppUserRepository
 *     class JpaRepository~AppUser, UUID~
 *     class AppUserRepository
 *     JpaAppUserRepository --|> JpaRepository~AppUser, UUID~
 *     JpaAppUserRepository --|> AppUserRepository
 * </div>
 *
 * @see AppUserRepository
 * @see AppUser
 */
@Repository
public interface JpaAppUserRepository
        extends JpaRepository<AppUser, UUID>,
        AppUserRepository {

    @Override
    Optional<AppUser> findByUsername(String username);

    @Override
    @Query("SELECT u FROM AppUser u"
            + " WHERE u.organization.id = :orgId"
            + " ORDER BY u.username")
    List<AppUser> findByOrganizationId(UUID orgId);
}
