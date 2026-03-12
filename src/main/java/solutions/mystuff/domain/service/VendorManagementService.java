package solutions.mystuff.domain.service;

import java.util.List;
import java.util.UUID;

import solutions.mystuff.domain.model.Vendor;
import solutions.mystuff.domain.port.in.VendorManagement;
import solutions.mystuff.domain.port.out.VendorRepository;
import org.springframework.stereotype.Service;

/**
 * Resolves, creates, updates, and deletes vendors.
 *
 * <div class="mermaid">
 * sequenceDiagram
 *     Controller->>VendorManagementService: resolveVendor(...)
 *     alt vendorId == "__new__"
 *         VendorManagementService->>VendorRepository: save(newVendor)
 *     else existing vendor
 *         VendorManagementService->>VendorRepository: findByIdAndOrganizationId(...)
 *     end
 * </div>
 *
 * @see VendorManagement
 * @see VendorRepository
 */
@Service
public class VendorManagementService
        implements VendorManagement {

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
            return findVendor(orgId,
                    UUID.fromString(vendorId));
        }
        return null;
    }

    @Override
    public Vendor updateVendor(
            UUID orgId, UUID vendorId,
            String name, String phone, String email,
            String addressLine1, String addressLine2,
            String city, String stateProvince,
            String postalCode, String country,
            String website, String notes) {
        requireName(name);
        Vendor vendor = vendorId != null
                ? findVendor(orgId, vendorId)
                : newVendor(orgId);
        applyFields(vendor, name, phone, email,
                addressLine1, addressLine2, city,
                stateProvince, postalCode, country,
                website, notes);
        return vendorRepository.save(vendor);
    }

    private Vendor newVendor(UUID orgId) {
        Vendor v = new Vendor();
        v.setOrganizationId(orgId);
        return v;
    }

    private void applyFields(
            Vendor v, String name, String phone,
            String email, String addressLine1,
            String addressLine2, String city,
            String stateProvince, String postalCode,
            String country, String website,
            String notes) {
        v.setName(name.trim());
        v.setPhone(trimOrNull(phone));
        v.setEmail(trimOrNull(email));
        v.setAddressLine1(trimOrNull(addressLine1));
        v.setAddressLine2(trimOrNull(addressLine2));
        v.setCity(trimOrNull(city));
        v.setStateProvince(trimOrNull(stateProvince));
        v.setPostalCode(trimOrNull(postalCode));
        v.setCountry(trimOrNull(country));
        v.setWebsite(trimOrNull(website));
        v.setNotes(trimOrNull(notes));
    }

    @Override
    public void deleteVendor(
            UUID orgId, UUID vendorId) {
        findVendor(orgId, vendorId);
        vendorRepository.deleteByIdAndOrganizationId(
                vendorId, orgId);
    }

    @Override
    public List<Vendor> findAllVendors(UUID orgId) {
        return vendorRepository
                .findByOrganizationId(orgId);
    }

    private Vendor createVendor(
            UUID orgId, String name, String phone) {
        requireName(name);
        requireMaxLength(name, "Vendor name",
                VendorFieldLimits.MAX_NAME);
        requireMaxLength(phone, "Vendor phone",
                VendorFieldLimits.MAX_PHONE);
        Vendor vendor = new Vendor();
        vendor.setOrganizationId(orgId);
        vendor.setName(name.trim());
        if (phone != null && !phone.isBlank()) {
            vendor.setPhone(phone.trim());
        }
        return vendorRepository.save(vendor);
    }

    private Vendor findVendor(
            UUID orgId, UUID vendorId) {
        return vendorRepository
                .findByIdAndOrganizationId(
                        vendorId, orgId)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Vendor not found"));
    }

    private void requireName(String name) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException(
                    "Vendor name is required");
        }
    }

    private void requireMaxLength(
            String value, String field, int max) {
        if (value != null
                && value.trim().length() > max) {
            throw new IllegalArgumentException(
                    field + " exceeds maximum length"
                            + " of " + max);
        }
    }

    private String trimOrNull(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }
        return value.trim();
    }
}
