package solutions.mystuff.domain.port.in;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import solutions.mystuff.domain.model.Facility;

/**
 * Inbound port for read-only facility queries.
 *
 * <div class="mermaid">
 * classDiagram
 *     class FacilityQuery {
 *         +findAllFacilities(UUID) List~Facility~
 *         +findFacility(UUID, UUID) Optional~Facility~
 *     }
 *     FacilityManagementService ..|&gt; FacilityQuery
 * </div>
 *
 * @see solutions.mystuff.domain.model.Facility
 */
public interface FacilityQuery {

    /** Find all facilities for an organization. */
    List<Facility> findAllFacilities(UUID orgId);

    /** Find a single facility by ID within an organization. */
    Optional<Facility> findFacility(
            UUID facilityId, UUID orgId);
}
