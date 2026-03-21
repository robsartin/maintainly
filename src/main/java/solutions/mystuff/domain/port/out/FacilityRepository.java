package solutions.mystuff.domain.port.out;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import solutions.mystuff.domain.model.Facility;

/**
 * Outbound port for querying and persisting facilities.
 *
 * <div class="mermaid">
 * classDiagram
 *     class FacilityRepository {
 *         +findByOrganizationId(UUID) List~Facility~
 *         +findByIdAndOrganizationId(UUID, UUID) Optional~Facility~
 *     }
 *     JpaFacilityRepositoryAdapter ..|> FacilityRepository
 * </div>
 *
 * @see solutions.mystuff.domain.model.Facility
 */
public interface FacilityRepository {

    /** Find all facilities belonging to an organization. */
    List<Facility> findByOrganizationId(
            UUID organizationId);

    /** Find a single facility by ID scoped to an organization. */
    Optional<Facility> findByIdAndOrganizationId(
            UUID id, UUID organizationId);
}
