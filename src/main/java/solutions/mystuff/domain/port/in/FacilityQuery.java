package solutions.mystuff.domain.port.in;

import java.util.List;
import java.util.UUID;

import solutions.mystuff.domain.model.Facility;

/**
 * Inbound port for read-only facility queries.
 *
 * <div class="mermaid">
 * classDiagram
 *     class FacilityQuery {
 *         +findByOrganization(UUID) List~Facility~
 *     }
 *     FacilityQueryService ..|> FacilityQuery
 * </div>
 *
 * @see solutions.mystuff.domain.model.Facility
 */
public interface FacilityQuery {

    /** Find all facilities belonging to an organization. */
    List<Facility> findByOrganization(UUID orgId);
}
