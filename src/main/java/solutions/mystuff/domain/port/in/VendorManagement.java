package solutions.mystuff.domain.port.in;

import java.util.UUID;

import solutions.mystuff.domain.model.Vendor;
import solutions.mystuff.domain.model.VendorData;

/**
 * Inbound port for vendor command operations (create, update, delete).
 *
 * @see solutions.mystuff.domain.model.Vendor
 * @see VendorData
 */
public interface VendorManagement {

    /** Create a new vendor with all fields. */
    Vendor createVendor(UUID orgId, VendorData data);

    /** Create a new vendor with just name and phone (inline creation). */
    Vendor createVendor(UUID orgId, String name,
            String phone);

    /** Update all fields on an existing vendor. */
    Vendor updateVendor(UUID orgId, UUID vendorId,
            VendorData data);

    /** Delete a vendor by ID. */
    void deleteVendor(UUID orgId, UUID vendorId);
}
