package solutions.mystuff.domain.service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import solutions.mystuff.domain.model.Facility;
import solutions.mystuff.domain.port.in.FacilityQuery;
import solutions.mystuff.domain.port.out.FacilityRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation
        .Transactional;

/**
 * Provides read-only access to facility data.
 *
 * <div class="mermaid">
 * sequenceDiagram
 *     Controller->>FacilityQueryService: findAllFacilities
 *     FacilityQueryService->>FacilityRepository: findByOrganizationId
 *     FacilityRepository-->>Controller: List of Facility
 * </div>
 *
 * @see FacilityQuery
 */
@Service
@Transactional(readOnly = true)
public class FacilityQueryService
        implements FacilityQuery {

    private final FacilityRepository facilityRepo;

    /** Creates a service backed by the given repository. */
    public FacilityQueryService(
            FacilityRepository facilityRepo) {
        this.facilityRepo = facilityRepo;
    }

    /** {@inheritDoc} */
    @Override
    public List<Facility> findAllFacilities(UUID orgId) {
        return facilityRepo.findByOrganizationId(orgId);
    }

    /** {@inheritDoc} */
    @Override
    public Optional<Facility> findFacility(
            UUID facilityId, UUID orgId) {
        return facilityRepo
                .findByIdAndOrganizationId(
                        facilityId, orgId);
    }
}
