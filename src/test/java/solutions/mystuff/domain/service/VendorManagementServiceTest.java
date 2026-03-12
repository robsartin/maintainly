package solutions.mystuff.domain.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import solutions.mystuff.domain.model.Vendor;
import solutions.mystuff.domain.port.out.VendorRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions
        .assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@DisplayName("VendorManagementService")
class VendorManagementServiceTest {

    private final VendorRepository repo =
            mock(VendorRepository.class);
    private final VendorManagementService service =
            new VendorManagementService(repo);

    private final UUID orgId = UUID.randomUUID();

    @Test
    @DisplayName("should resolve existing vendor by ID")
    void shouldResolveExisting() {
        UUID vendorId = UUID.randomUUID();
        Vendor vendor = new Vendor();
        vendor.setName("Existing");
        when(repo.findByIdAndOrganizationId(
                vendorId, orgId))
                .thenReturn(Optional.of(vendor));

        Vendor result = service.resolveVendor(
                orgId, vendorId.toString(), null, null);

        assertThat(result.getName())
                .isEqualTo("Existing");
    }

    @Test
    @DisplayName("should return null for blank vendor ID")
    void shouldReturnNullForBlank() {
        Vendor result = service.resolveVendor(
                orgId, null, null, null);
        assertThat(result).isNull();
    }

    @Test
    @DisplayName("should create new vendor")
    void shouldCreateNewVendor() {
        when(repo.save(any(Vendor.class)))
                .thenAnswer(i -> i.getArgument(0));

        Vendor result = service.resolveVendor(
                orgId, "__new__", "Acme", "555-1234");

        assertThat(result.getName()).isEqualTo("Acme");
        assertThat(result.getPhone())
                .isEqualTo("555-1234");
        verify(repo).save(any(Vendor.class));
    }

    @Test
    @DisplayName("should reject blank vendor name")
    void shouldRejectBlankName() {
        assertThatThrownBy(() ->
                service.resolveVendor(
                        orgId, "__new__", "  ", null))
                .isInstanceOf(
                        IllegalArgumentException.class)
                .hasMessageContaining("required");
    }

    @Test
    @DisplayName("should reject long vendor name")
    void shouldRejectLongName() {
        String longName = "x".repeat(201);
        assertThatThrownBy(() ->
                service.resolveVendor(
                        orgId, "__new__",
                        longName, null))
                .isInstanceOf(
                        IllegalArgumentException.class)
                .hasMessageContaining("maximum length");
    }

    @Test
    @DisplayName("should throw when vendor not found")
    void shouldThrowWhenNotFound() {
        UUID vendorId = UUID.randomUUID();
        when(repo.findByIdAndOrganizationId(
                vendorId, orgId))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() ->
                service.resolveVendor(
                        orgId, vendorId.toString(),
                        null, null))
                .isInstanceOf(
                        IllegalArgumentException.class)
                .hasMessage("Vendor not found");
    }

    @Test
    @DisplayName("should update all vendor fields")
    void shouldUpdateAllFields() {
        UUID vendorId = UUID.randomUUID();
        Vendor existing = existingVendor(vendorId);
        when(repo.findByIdAndOrganizationId(
                vendorId, orgId))
                .thenReturn(Optional.of(existing));
        when(repo.save(any(Vendor.class)))
                .thenAnswer(i -> i.getArgument(0));

        Vendor result = service.updateVendor(
                orgId, vendorId, "Updated", "555-9999",
                "new@test.com", "456 Oak", "Apt 2",
                "Chicago", "IL", "60601", "US",
                "https://updated.com", "Great vendor");

        assertThat(result.getName())
                .isEqualTo("Updated");
        assertThat(result.getPhone())
                .isEqualTo("555-9999");
        assertThat(result.getEmail())
                .isEqualTo("new@test.com");
        assertThat(result.getAddressLine1())
                .isEqualTo("456 Oak");
        assertThat(result.getWebsite())
                .isEqualTo("https://updated.com");
        assertThat(result.getNotes())
                .isEqualTo("Great vendor");
        verify(repo).save(existing);
    }

    @Test
    @DisplayName("should create vendor with all fields")
    void shouldCreateWithAllFields() {
        when(repo.save(any(Vendor.class)))
                .thenAnswer(i -> i.getArgument(0));

        Vendor result = service.updateVendor(
                orgId, null, "New Corp", "555-1111",
                "info@new.com", "789 Pine", null,
                "Boston", "MA", "02101", "US",
                "https://new.com", "Notes here");

        assertThat(result.getName())
                .isEqualTo("New Corp");
        assertThat(result.getOrganizationId())
                .isEqualTo(orgId);
        assertThat(result.getCity())
                .isEqualTo("Boston");
    }

    @Test
    @DisplayName("should reject blank name on update")
    void shouldRejectBlankNameOnUpdate() {
        UUID vendorId = UUID.randomUUID();
        assertThatThrownBy(() ->
                service.updateVendor(
                        orgId, vendorId, "  ", null,
                        null, null, null, null,
                        null, null, null, null, null))
                .isInstanceOf(
                        IllegalArgumentException.class)
                .hasMessageContaining("required");
    }

    @Test
    @DisplayName("should throw on update when not found")
    void shouldThrowOnUpdateNotFound() {
        UUID vendorId = UUID.randomUUID();
        when(repo.findByIdAndOrganizationId(
                vendorId, orgId))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() ->
                service.updateVendor(
                        orgId, vendorId, "Name", null,
                        null, null, null, null,
                        null, null, null, null, null))
                .isInstanceOf(
                        IllegalArgumentException.class)
                .hasMessage("Vendor not found");
    }

    @Test
    @DisplayName("should delete vendor")
    void shouldDeleteVendor() {
        UUID vendorId = UUID.randomUUID();
        when(repo.findByIdAndOrganizationId(
                vendorId, orgId))
                .thenReturn(Optional.of(new Vendor()));

        service.deleteVendor(orgId, vendorId);

        verify(repo).deleteByIdAndOrganizationId(
                vendorId, orgId);
    }

    @Test
    @DisplayName("should throw on delete when not found")
    void shouldThrowOnDeleteNotFound() {
        UUID vendorId = UUID.randomUUID();
        when(repo.findByIdAndOrganizationId(
                vendorId, orgId))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() ->
                service.deleteVendor(orgId, vendorId))
                .isInstanceOf(
                        IllegalArgumentException.class)
                .hasMessage("Vendor not found");
    }

    @Test
    @DisplayName("should find all vendors")
    void shouldFindAllVendors() {
        List<Vendor> vendors =
                List.of(new Vendor(), new Vendor());
        when(repo.findByOrganizationId(orgId))
                .thenReturn(vendors);

        List<Vendor> result =
                service.findAllVendors(orgId);

        assertThat(result).hasSize(2);
    }

    private Vendor existingVendor(UUID vendorId) {
        Vendor v = new Vendor();
        v.setName("Old Name");
        v.setOrganizationId(orgId);
        v.setAltPhones(new ArrayList<>());
        return v;
    }
}
