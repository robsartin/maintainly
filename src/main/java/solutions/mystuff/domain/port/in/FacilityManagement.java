package solutions.mystuff.domain.port.in;

import java.util.UUID;

import solutions.mystuff.domain.model.Facility;
import solutions.mystuff.domain.model.FacilityData;

/**
 * Inbound port for facility command operations
 * (create, update, delete).
 *
 * <div class="mermaid">
 * classDiagram
 *     class FacilityManagement {
 *         +createFacility(UUID, FacilityData) Facility
 *         +updateFacility(UUID, UUID, FacilityData) Facility
 *         +deleteFacility(UUID, UUID) void
 *     }
 *     FacilityManagementService ..|&gt; FacilityManagement
 * </div>
 *
 * @see solutions.mystuff.domain.model.Facility
 * @see FacilityData
 */
public interface FacilityManagement {

    /** Create a new facility for the organization. */
    Facility createFacility(
            UUID orgId, FacilityData data);

    /** Update all fields on an existing facility. */
    Facility updateFacility(
            UUID orgId, UUID facilityId,
            FacilityData data);

    /** Delete a facility by ID. */
    void deleteFacility(UUID orgId, UUID facilityId);
}
