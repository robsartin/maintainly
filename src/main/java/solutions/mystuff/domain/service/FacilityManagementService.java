package solutions.mystuff.domain.service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import solutions.mystuff.domain.model.Facility;
import solutions.mystuff.domain.model.FacilityData;
import solutions.mystuff.domain.model.NotFoundException;
import solutions.mystuff.domain.model.Validation;
import solutions.mystuff.domain.port.in.FacilityManagement;
import solutions.mystuff.domain.port.in.FacilityQuery;
import solutions.mystuff.domain.port.out.FacilityRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation
        .Transactional;

/**
 * Creates, updates, deletes, and queries facilities.
 *
 * <div class="mermaid">
 * sequenceDiagram
 *     participant C as Controller
 *     participant S as FacilityManagementService
 *     participant R as FacilityRepository
 *     C-&gt;&gt;S: create/update/delete/find
 *     S-&gt;&gt;S: validate fields
 *     S-&gt;&gt;R: save / find / delete
 *     R--&gt;&gt;S: Facility
 *     S--&gt;&gt;C: result
 * </div>
 *
 * @see FacilityManagement
 * @see FacilityQuery
 * @see FacilityRepository
 */
@Service
public class FacilityManagementService
        implements FacilityManagement, FacilityQuery {

    private static final Logger log =
            LoggerFactory.getLogger(
                    FacilityManagementService.class);
    private static final int MAX_NAME = 200;
    private static final int MAX_ADDRESS = 200;
    private static final int MAX_CITY = 100;
    private static final int MAX_STATE = 100;
    private static final int MAX_POSTAL = 30;
    private static final int MAX_COUNTRY = 100;

    private final FacilityRepository facilityRepo;

    public FacilityManagementService(
            FacilityRepository facilityRepo) {
        this.facilityRepo = facilityRepo;
    }

    @Override
    @Transactional
    public Facility createFacility(
            UUID orgId, FacilityData data) {
        String trimName = validateFields(data);
        Facility facility = new Facility();
        facility.setOrganizationId(orgId);
        facility.setName(trimName);
        applyFields(facility, data);
        Facility saved = facilityRepo.save(facility);
        log.info("Created facility {}",
                saved.getName());
        return saved;
    }

    @Override
    @Transactional
    public Facility updateFacility(
            UUID orgId, UUID facilityId,
            FacilityData data) {
        String trimName = validateFields(data);
        Facility facility =
                requireFacility(orgId, facilityId);
        facility.setName(trimName);
        applyFields(facility, data);
        Facility saved = facilityRepo.save(facility);
        log.info("Updated facility {}",
                saved.getName());
        return saved;
    }

    @Override
    @Transactional
    public void deleteFacility(
            UUID orgId, UUID facilityId) {
        Facility facility =
                requireFacility(orgId, facilityId);
        facilityRepo.deleteByIdAndOrganizationId(
                facilityId, orgId);
        log.info("Deleted facility {}",
                facility.getName());
    }

    @Override
    @Transactional(readOnly = true)
    public List<Facility> findAllFacilities(
            UUID orgId) {
        return facilityRepo
                .findByOrganizationId(orgId);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Facility> findFacility(
            UUID facilityId, UUID orgId) {
        return facilityRepo
                .findByIdAndOrganizationId(
                        facilityId, orgId);
    }

    private String validateFields(FacilityData data) {
        String trimName = Validation.requireNotBlank(
                data.name(), "Facility name");
        Validation.requireMaxLength(
                trimName, "Facility name", MAX_NAME);
        Validation.requireMaxLength(
                data.addressLine1(),
                "Address line 1", MAX_ADDRESS);
        Validation.requireMaxLength(
                data.addressLine2(),
                "Address line 2", MAX_ADDRESS);
        Validation.requireMaxLength(
                data.city(), "City", MAX_CITY);
        Validation.requireMaxLength(
                data.stateProvince(),
                "State/province", MAX_STATE);
        Validation.requireMaxLength(
                data.postalCode(),
                "Postal code", MAX_POSTAL);
        Validation.requireMaxLength(
                data.country(), "Country",
                MAX_COUNTRY);
        return trimName;
    }

    private void applyFields(
            Facility f, FacilityData data) {
        f.setAddressLine1(Validation.trimOrNull(
                data.addressLine1()));
        f.setAddressLine2(Validation.trimOrNull(
                data.addressLine2()));
        f.setCity(Validation.trimOrNull(data.city()));
        f.setStateProvince(Validation.trimOrNull(
                data.stateProvince()));
        f.setPostalCode(Validation.trimOrNull(
                data.postalCode()));
        f.setCountry(Validation.trimOrNull(
                data.country()));
    }

    private Facility requireFacility(
            UUID orgId, UUID facilityId) {
        return facilityRepo
                .findByIdAndOrganizationId(
                        facilityId, orgId)
                .orElseThrow(() ->
                        new NotFoundException(
                                "Facility not found"));
    }
}
