package solutions.mystuff.domain.port.in;

import java.util.UUID;

import solutions.mystuff.domain.model.Vendor;

/**
 * Inbound port for resolving or creating vendors.
 *
 * <div class="mermaid">
 * classDiagram
 *     class VendorManagement {
 *         +resolveVendor(UUID, String, String, String) Vendor
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
}
