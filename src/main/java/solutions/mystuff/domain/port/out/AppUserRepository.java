package solutions.mystuff.domain.port.out;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import solutions.mystuff.domain.model.AppUser;

/**
 * Outbound port for persisting and retrieving application users.
 *
 * <div class="mermaid">
 * classDiagram
 *     class AppUserRepository {
 *         +findByUsername(String) Optional~AppUser~
 *         +findById(UUID) Optional~AppUser~
 *         +findByOrganizationId(UUID) List~AppUser~
 *         +save(AppUser) AppUser
 *     }
 *     JpaAppUserRepository ..|> AppUserRepository
 * </div>
 *
 * @see solutions.mystuff.domain.model.AppUser
 */
public interface AppUserRepository {

    /** Find a user by their unique username. */
    Optional<AppUser> findByUsername(String username);

    /** Find a user by their unique identifier. */
    Optional<AppUser> findById(UUID id);

    /** Find all users belonging to an organization. */
    List<AppUser> findByOrganizationId(UUID orgId);

    /** Persist a new or updated user. */
    AppUser save(AppUser user);
}
