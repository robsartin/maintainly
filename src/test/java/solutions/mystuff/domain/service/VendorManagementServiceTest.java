package solutions.mystuff.domain.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import solutions.mystuff.domain.model.NotFoundException;
import solutions.mystuff.domain.model.ParsedAltPhone;
import solutions.mystuff.domain.model.Vendor;
import solutions.mystuff.domain.model.VendorData;
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
    @DisplayName("should create vendor with name and phone")
    void shouldCreateWithNameAndPhone() {
        when(repo.save(any(Vendor.class)))
                .thenAnswer(i -> i.getArgument(0));

        Vendor result = service.createVendor(
                orgId, "Acme", "555-1234");

        assertThat(result.getName()).isEqualTo("Acme");
        assertThat(result.getPhone())
                .isEqualTo("555-1234");
        verify(repo).save(any(Vendor.class));
    }

    @Test
    @DisplayName("should reject blank vendor name on inline create")
    void shouldRejectBlankNameInline() {
        assertThatThrownBy(() ->
                service.createVendor(
                        orgId, "  ", null))
                .isInstanceOf(
                        IllegalArgumentException.class)
                .hasMessageContaining("required");
    }

    @Test
    @DisplayName("should reject long vendor name on inline create")
    void shouldRejectLongNameInline() {
        String longName = "x".repeat(201);
        assertThatThrownBy(() ->
                service.createVendor(
                        orgId, longName, null))
                .isInstanceOf(
                        IllegalArgumentException.class)
                .hasMessageContaining("maximum length");
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

        VendorData data = new VendorData(
                "Updated", "555-9999", "new@test.com",
                "456 Oak", "Apt 2", "Chicago", "IL",
                "60601", "US", "https://updated.com",
                "Great vendor", List.of());
        Vendor result = service.updateVendor(
                orgId, vendorId, data);

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

        VendorData data = new VendorData(
                "New Corp", "555-1111", "info@new.com",
                "789 Pine", null, "Boston", "MA",
                "02101", "US", "https://new.com",
                "Notes here", List.of());
        Vendor result = service.createVendor(
                orgId, data);

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
        VendorData data = new VendorData(
                "  ", null, null, null, null,
                null, null, null, null, null, null,
                List.of());
        assertThatThrownBy(() ->
                service.updateVendor(
                        orgId, vendorId, data))
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

        VendorData data = new VendorData(
                "Name", null, null, null, null,
                null, null, null, null, null, null,
                List.of());
        assertThatThrownBy(() ->
                service.updateVendor(
                        orgId, vendorId, data))
                .isInstanceOf(
                        NotFoundException.class)
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
                        NotFoundException.class)
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

    @Test
    @DisplayName("should create vendor with alt phones")
    void shouldCreateWithAltPhones() {
        when(repo.save(any(Vendor.class)))
                .thenAnswer(i -> i.getArgument(0));

        List<ParsedAltPhone> altPhones = List.of(
                new ParsedAltPhone("555-2222", "mobile"),
                new ParsedAltPhone("555-3333", null));
        VendorData data = new VendorData(
                "Alt Corp", "555-1111", null,
                null, null, null, null,
                null, null, null, null, altPhones);
        Vendor result = service.createVendor(
                orgId, data);

        assertThat(result.getAltPhones()).hasSize(2);
        assertThat(result.getAltPhones().get(0)
                .getPhone()).isEqualTo("555-2222");
        assertThat(result.getAltPhones().get(0)
                .getLabel()).isEqualTo("mobile");
        assertThat(result.getAltPhones().get(1)
                .getLabel()).isNull();
    }

    @Test
    @DisplayName("should update vendor alt phones")
    void shouldUpdateAltPhones() {
        UUID vendorId = UUID.randomUUID();
        Vendor existing = existingVendor(vendorId);
        when(repo.findByIdAndOrganizationId(
                vendorId, orgId))
                .thenReturn(Optional.of(existing));
        when(repo.save(any(Vendor.class)))
                .thenAnswer(i -> i.getArgument(0));

        List<ParsedAltPhone> altPhones = List.of(
                new ParsedAltPhone("555-4444", "work"));
        VendorData data = new VendorData(
                "Updated", null, null,
                null, null, null, null,
                null, null, null, null, altPhones);
        Vendor result = service.updateVendor(
                orgId, vendorId, data);

        assertThat(result.getAltPhones()).hasSize(1);
        assertThat(result.getAltPhones().get(0)
                .getPhone()).isEqualTo("555-4444");
    }

    @Test
    @DisplayName("should skip blank alt phones")
    void shouldSkipBlankAltPhones() {
        when(repo.save(any(Vendor.class)))
                .thenAnswer(i -> i.getArgument(0));

        List<ParsedAltPhone> altPhones = List.of(
                new ParsedAltPhone("  ", "mobile"),
                new ParsedAltPhone("555-5555", null));
        VendorData data = new VendorData(
                "Corp", null, null,
                null, null, null, null,
                null, null, null, null, altPhones);
        Vendor result = service.createVendor(
                orgId, data);

        assertThat(result.getAltPhones()).hasSize(1);
    }

    @Test
    @DisplayName("should reject alt phone exceeding max length")
    void shouldRejectLongAltPhone() {
        String longPhone = "x".repeat(51);
        List<ParsedAltPhone> altPhones = List.of(
                new ParsedAltPhone(longPhone, null));
        VendorData data = new VendorData(
                "Corp", null, null,
                null, null, null, null,
                null, null, null, null, altPhones);
        assertThatThrownBy(() ->
                service.createVendor(orgId, data))
                .isInstanceOf(
                        IllegalArgumentException.class)
                .hasMessageContaining("maximum length");
    }

    @Test
    @DisplayName("should reject invalid email on create")
    void shouldRejectInvalidEmailOnCreate() {
        VendorData data = new VendorData(
                "Corp", null, "not-an-email",
                null, null, null, null,
                null, null, null, null, List.of());
        assertThatThrownBy(() ->
                service.createVendor(orgId, data))
                .isInstanceOf(
                        IllegalArgumentException.class)
                .hasMessageContaining("email");
    }

    @Test
    @DisplayName("should accept valid email on create")
    void shouldAcceptValidEmailOnCreate() {
        when(repo.save(any(Vendor.class)))
                .thenAnswer(i -> i.getArgument(0));

        VendorData data = new VendorData(
                "Corp", null, "test@example.com",
                null, null, null, null,
                null, null, null, null, List.of());
        Vendor result = service.createVendor(
                orgId, data);

        assertThat(result.getEmail())
                .isEqualTo("test@example.com");
    }

    @Test
    @DisplayName("should accept null email on create")
    void shouldAcceptNullEmailOnCreate() {
        when(repo.save(any(Vendor.class)))
                .thenAnswer(i -> i.getArgument(0));

        VendorData data = new VendorData(
                "Corp", null, null,
                null, null, null, null,
                null, null, null, null, List.of());
        Vendor result = service.createVendor(
                orgId, data);

        assertThat(result.getEmail()).isNull();
    }

    private Vendor existingVendor(UUID vendorId) {
        Vendor v = new Vendor();
        v.setName("Old Name");
        v.setOrganizationId(orgId);
        v.setAltPhones(new ArrayList<>());
        return v;
    }
}
