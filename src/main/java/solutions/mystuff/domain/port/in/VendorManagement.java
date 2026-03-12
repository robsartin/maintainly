package solutions.mystuff.domain.port.in;

import java.util.List;
import java.util.UUID;

import solutions.mystuff.domain.model.Vendor;

/**
 * Inbound port for vendor CRUD operations.
 *
 * <div class="mermaid">
 * classDiagram
 *     class VendorManagement {
 *         +resolveVendor(UUID, String, String, String) Vendor
 *         +updateVendor(UUID, UUID, String, ...) Vendor
 *         +deleteVendor(UUID, UUID) void
 *         +findAllVendors(UUID) List~Vendor~
 *     }
 *     VendorManagementService ..|> VendorManagement
 * </div>
 *
 * @see solutions.mystuff.domain.model.Vendor
 */
public interface VendorManagement {

    /** Resolve an existing vendor by ID or create a new one. */
    Vendor resolveVendor(UUID orgId, String vendorId,
            String newVendorName,
            String newVendorPhone);

    /** Update all fields on an existing vendor. */
    Vendor updateVendor(UUID orgId, UUID vendorId,
            String name, String phone, String email,
            String addressLine1, String addressLine2,
            String city, String stateProvince,
            String postalCode, String country,
            String website, String notes);

    /** Delete a vendor by ID. */
    void deleteVendor(UUID orgId, UUID vendorId);

    /** Find all vendors for an organization. */
    List<Vendor> findAllVendors(UUID orgId);
}
