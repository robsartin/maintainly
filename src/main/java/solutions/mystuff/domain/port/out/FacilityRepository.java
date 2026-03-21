package solutions.mystuff.domain.port.out;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import solutions.mystuff.domain.model.Facility;

/**
 * Outbound port for persisting and retrieving facilities.
 *
 * <div class="mermaid">
 * classDiagram
 *     class FacilityRepository {
 *         +findByOrganizationId(UUID) List~Facility~
 *         +findByIdAndOrganizationId(UUID, UUID) Optional~Facility~
 *         +save(Facility) Facility
 *         +deleteByIdAndOrganizationId(UUID, UUID) void
 *     }
 *     JpaFacilityRepository ..|&gt; FacilityRepository
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

    /** Persist a new or updated facility. */
    Facility save(Facility facility);

    /** Delete a facility by ID scoped to an organization. */
    void deleteByIdAndOrganizationId(
            UUID id, UUID organizationId);
}
