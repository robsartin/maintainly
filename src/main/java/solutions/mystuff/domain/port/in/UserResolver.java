package solutions.mystuff.domain.port.in;

import solutions.mystuff.domain.model.AppUser;

/**
 * Inbound port for resolving or creating application users.
 *
 * <div class="mermaid">
 * classDiagram
 *     class UserResolver {
 *         +resolveOrCreate(String) AppUser
 *     }
 *     UserResolverConfiguration ..|> UserResolver
 * </div>
 *
 * @see solutions.mystuff.domain.model.AppUser
 */
public interface UserResolver {

    /** Resolve an existing user by username or create a new one. */
    AppUser resolveOrCreate(String username);
}
