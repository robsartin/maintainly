package solutions.mystuff.domain.service;

import java.util.UUID;

import solutions.mystuff.domain.model.Vendor;
import solutions.mystuff.domain.port.in.VendorManagement;
import solutions.mystuff.domain.port.out.VendorRepository;
import org.springframework.stereotype.Service;

@Service
public class VendorManagementService
        implements VendorManagement {

    private static final int MAX_NAME_LENGTH = 200;
    private static final int MAX_PHONE_LENGTH = 50;

    private final VendorRepository vendorRepository;

    public VendorManagementService(
            VendorRepository vendorRepository) {
        this.vendorRepository = vendorRepository;
    }

    @Override
    public Vendor resolveVendor(
            UUID orgId, String vendorId,
            String newVendorName,
            String newVendorPhone) {
        if ("__new__".equals(vendorId)) {
            return createVendor(orgId,
                    newVendorName, newVendorPhone);
        }
        if (vendorId != null && !vendorId.isBlank()) {
            return vendorRepository
                    .findByIdAndOrganizationId(
                            UUID.fromString(vendorId),
                            orgId)
                    .orElseThrow(() ->
                            new IllegalArgumentException(
                                    "Vendor not found"));
        }
        return null;
    }

    private Vendor createVendor(
            UUID orgId, String name, String phone) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException(
                    "Vendor name is required");
        }
        if (name.trim().length() > MAX_NAME_LENGTH) {
            throw new IllegalArgumentException(
                    "Vendor name exceeds maximum length"
                            + " of " + MAX_NAME_LENGTH);
        }
        if (phone != null
                && phone.trim().length()
                        > MAX_PHONE_LENGTH) {
            throw new IllegalArgumentException(
                    "Vendor phone exceeds maximum length"
                            + " of " + MAX_PHONE_LENGTH);
        }
        Vendor vendor = new Vendor();
        vendor.setOrganizationId(orgId);
        vendor.setName(name.trim());
        if (phone != null && !phone.isBlank()) {
            vendor.setPhone(phone.trim());
        }
        return vendorRepository.save(vendor);
    }
}
