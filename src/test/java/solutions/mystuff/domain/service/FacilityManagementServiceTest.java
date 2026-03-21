package solutions.mystuff.domain.service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import solutions.mystuff.domain.model.Facility;
import solutions.mystuff.domain.model.FacilityData;
import solutions.mystuff.domain.model.NotFoundException;
import solutions.mystuff.domain.port.out
        .FacilityRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions
        .assertThat;
import static org.assertj.core.api.Assertions
        .assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@DisplayName("FacilityManagementService")
class FacilityManagementServiceTest {

    private final FacilityRepository repo =
            mock(FacilityRepository.class);
    private final FacilityManagementService service =
            new FacilityManagementService(repo);

    private final UUID orgId = UUID.randomUUID();

    @Test
    @DisplayName("should create facility with all fields")
    void shouldCreateWithAllFields() {
        when(repo.save(any(Facility.class)))
                .thenAnswer(i -> i.getArgument(0));

        FacilityData data = new FacilityData(
                "Main Office", "123 Main St",
                "Suite 100", "Springfield", "IL",
                "62701", "US");
        Facility result =
                service.createFacility(orgId, data);

        assertThat(result.getName())
                .isEqualTo("Main Office");
        assertThat(result.getOrganizationId())
                .isEqualTo(orgId);
        assertThat(result.getAddressLine1())
                .isEqualTo("123 Main St");
        assertThat(result.getCity())
                .isEqualTo("Springfield");
        assertThat(result.getStateProvince())
                .isEqualTo("IL");
        assertThat(result.getPostalCode())
                .isEqualTo("62701");
        assertThat(result.getCountry())
                .isEqualTo("US");
        verify(repo).save(any(Facility.class));
    }

    @Test
    @DisplayName("should reject blank facility name")
    void shouldRejectBlankName() {
        FacilityData data = new FacilityData(
                "  ", null, null, null,
                null, null, null);
        assertThatThrownBy(() ->
                service.createFacility(orgId, data))
                .isInstanceOf(
                        IllegalArgumentException.class)
                .hasMessageContaining("required");
    }

    @Test
    @DisplayName("should reject long facility name")
    void shouldRejectLongName() {
        String longName = "x".repeat(201);
        FacilityData data = new FacilityData(
                longName, null, null, null,
                null, null, null);
        assertThatThrownBy(() ->
                service.createFacility(orgId, data))
                .isInstanceOf(
                        IllegalArgumentException.class)
                .hasMessageContaining("maximum length");
    }

    @Test
    @DisplayName("should update all facility fields")
    void shouldUpdateAllFields() {
        UUID facilityId = UUID.randomUUID();
        Facility existing =
                existingFacility(facilityId);
        when(repo.findByIdAndOrganizationId(
                facilityId, orgId))
                .thenReturn(Optional.of(existing));
        when(repo.save(any(Facility.class)))
                .thenAnswer(i -> i.getArgument(0));

        FacilityData data = new FacilityData(
                "Updated Office", "456 Oak Ave",
                null, "Chicago", "IL",
                "60601", "US");
        Facility result = service.updateFacility(
                orgId, facilityId, data);

        assertThat(result.getName())
                .isEqualTo("Updated Office");
        assertThat(result.getAddressLine1())
                .isEqualTo("456 Oak Ave");
        assertThat(result.getAddressLine2())
                .isNull();
        assertThat(result.getCity())
                .isEqualTo("Chicago");
        verify(repo).save(existing);
    }

    @Test
    @DisplayName("should reject blank name on update")
    void shouldRejectBlankNameOnUpdate() {
        UUID facilityId = UUID.randomUUID();
        FacilityData data = new FacilityData(
                "  ", null, null, null,
                null, null, null);
        assertThatThrownBy(() ->
                service.updateFacility(
                        orgId, facilityId, data))
                .isInstanceOf(
                        IllegalArgumentException.class)
                .hasMessageContaining("required");
    }

    @Test
    @DisplayName("should throw on update when not found")
    void shouldThrowOnUpdateNotFound() {
        UUID facilityId = UUID.randomUUID();
        when(repo.findByIdAndOrganizationId(
                facilityId, orgId))
                .thenReturn(Optional.empty());

        FacilityData data = new FacilityData(
                "Name", null, null, null,
                null, null, null);
        assertThatThrownBy(() ->
                service.updateFacility(
                        orgId, facilityId, data))
                .isInstanceOf(
                        NotFoundException.class)
                .hasMessage("Facility not found");
    }

    @Test
    @DisplayName("should delete facility")
    void shouldDeleteFacility() {
        UUID facilityId = UUID.randomUUID();
        when(repo.findByIdAndOrganizationId(
                facilityId, orgId))
                .thenReturn(Optional.of(
                        new Facility()));

        service.deleteFacility(orgId, facilityId);

        verify(repo).deleteByIdAndOrganizationId(
                facilityId, orgId);
    }

    @Test
    @DisplayName("should throw on delete when not found")
    void shouldThrowOnDeleteNotFound() {
        UUID facilityId = UUID.randomUUID();
        when(repo.findByIdAndOrganizationId(
                facilityId, orgId))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() ->
                service.deleteFacility(
                        orgId, facilityId))
                .isInstanceOf(
                        NotFoundException.class)
                .hasMessage("Facility not found");
    }

    @Test
    @DisplayName("should find all facilities")
    void shouldFindAllFacilities() {
        List<Facility> facilities =
                List.of(new Facility(), new Facility());
        when(repo.findByOrganizationId(orgId))
                .thenReturn(facilities);

        List<Facility> result =
                service.findAllFacilities(orgId);

        assertThat(result).hasSize(2);
    }

    @Test
    @DisplayName("should find facility by id")
    void shouldFindFacilityById() {
        UUID facilityId = UUID.randomUUID();
        Facility facility =
                existingFacility(facilityId);
        when(repo.findByIdAndOrganizationId(
                facilityId, orgId))
                .thenReturn(Optional.of(facility));

        Optional<Facility> result =
                service.findFacility(
                        facilityId, orgId);

        assertThat(result).isPresent();
        assertThat(result.get().getName())
                .isEqualTo("Test Facility");
    }

    @Test
    @DisplayName("should trim whitespace from name")
    void shouldTrimWhitespaceFromName() {
        when(repo.save(any(Facility.class)))
                .thenAnswer(i -> i.getArgument(0));

        FacilityData data = new FacilityData(
                "  Trimmed  ", null, null, null,
                null, null, null);
        Facility result =
                service.createFacility(orgId, data);

        assertThat(result.getName())
                .isEqualTo("Trimmed");
    }

    @Test
    @DisplayName("should set null for blank address fields")
    void shouldSetNullForBlankFields() {
        when(repo.save(any(Facility.class)))
                .thenAnswer(i -> i.getArgument(0));

        FacilityData data = new FacilityData(
                "Office", "  ", "  ", "  ",
                "  ", "  ", "  ");
        Facility result =
                service.createFacility(orgId, data);

        assertThat(result.getAddressLine1()).isNull();
        assertThat(result.getCity()).isNull();
        assertThat(result.getCountry()).isNull();
    }

    private Facility existingFacility(
            UUID facilityId) {
        Facility f = new Facility();
        f.setName("Test Facility");
        f.setOrganizationId(orgId);
        return f;
    }
}
