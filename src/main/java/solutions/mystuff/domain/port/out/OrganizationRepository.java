package solutions.mystuff.domain.port.out;

import java.util.Optional;
import java.util.UUID;

import solutions.mystuff.domain.model.Organization;

/**
 * Outbound port for persisting and retrieving organizations.
 *
 * <pre>{@code
 * classDiagram
 *     class OrganizationRepository {
 *         <<interface>>
 *         +findById(UUID) Optional~Organization~
 *         +save(Organization) Organization
 *     }
 *     JpaOrganizationRepository ..|> OrganizationRepository
 * }</pre>
 *
 * @see solutions.mystuff.domain.model.Organization
 */
public interface OrganizationRepository {

    /** Find an organization by its unique identifier. */
    Optional<Organization> findById(UUID id);

    /** Persist a new or updated organization. */
    Organization save(Organization organization);
}
