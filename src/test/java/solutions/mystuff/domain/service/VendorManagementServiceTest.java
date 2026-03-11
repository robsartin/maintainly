package solutions.mystuff.domain.service;

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
}
