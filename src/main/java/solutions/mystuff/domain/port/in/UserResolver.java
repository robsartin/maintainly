package solutions.mystuff.domain.port.in;

import solutions.mystuff.domain.model.AppUser;

/**
 * Inbound port for resolving or creating application users.
 *
 * <pre>{@code
 * classDiagram
 *     class UserResolver {
 *         <<interface>>
 *         +resolveOrCreate(String) AppUser
 *     }
 *     UserResolverConfiguration ..|> UserResolver
 * }</pre>
 *
 * @see solutions.mystuff.domain.model.AppUser
 */
public interface UserResolver {

    /** Resolve an existing user by username or create a new one. */
    AppUser resolveOrCreate(String username);
}
