package solutions.mystuff.domain.port.in;

import java.util.UUID;

import solutions.mystuff.domain.model.Vendor;

public interface VendorManagement {

    Vendor resolveVendor(UUID orgId, String vendorId,
            String newVendorName,
            String newVendorPhone);
}
