package solutions.mystuff.domain.service;

import java.util.List;
import java.util.UUID;

import solutions.mystuff.domain.model.NotFoundException;
import solutions.mystuff.domain.model.ParsedAltPhone;
import solutions.mystuff.domain.model.Validation;
import solutions.mystuff.domain.model.Vendor;
import solutions.mystuff.domain.model.VendorAltPhone;
import solutions.mystuff.domain.model.VendorData;
import solutions.mystuff.domain.port.in.VendorManagement;
import solutions.mystuff.domain.port.in.VendorQuery;
import solutions.mystuff.domain.port.out.VendorRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation
        .Transactional;

/**
 * Creates, updates, deletes, and queries vendors.
 *
 * @see VendorManagement
 * @see VendorQuery
 * @see VendorRepository
 */
@Service
public class VendorManagementService
        implements VendorManagement, VendorQuery {

    private final VendorRepository vendorRepository;

    public VendorManagementService(
            VendorRepository vendorRepository) {
        this.vendorRepository = vendorRepository;
    }

    @Override
    public Vendor createVendor(
            UUID orgId, VendorData data) {
        Validation.requireNotBlank(
                data.name(), "Vendor name");
        Vendor vendor = newVendor(orgId);
        applyFields(vendor, data);
        return vendorRepository.save(vendor);
    }

    @Override
    public Vendor createVendor(
            UUID orgId, String name, String phone) {
        Validation.requireNotBlank(name, "Vendor name");
        Validation.requireMaxLength(name, "Vendor name",
                VendorFieldLimits.MAX_NAME);
        Validation.requireMaxLength(phone, "Vendor phone",
                VendorFieldLimits.MAX_PHONE);
        Vendor vendor = newVendor(orgId);
        vendor.setName(name.trim());
        if (phone != null && !phone.isBlank()) {
            vendor.setPhone(phone.trim());
        }
        return vendorRepository.save(vendor);
    }

    @Override
    public Vendor updateVendor(
            UUID orgId, UUID vendorId,
            VendorData data) {
        Validation.requireNotBlank(
                data.name(), "Vendor name");
        Vendor vendor = findVendor(orgId, vendorId);
        applyFields(vendor, data);
        return vendorRepository.save(vendor);
    }

    @Override
    public void deleteVendor(
            UUID orgId, UUID vendorId) {
        findVendor(orgId, vendorId);
        vendorRepository.deleteByIdAndOrganizationId(
                vendorId, orgId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Vendor> findAllVendors(UUID orgId) {
        return vendorRepository
                .findByOrganizationId(orgId);
    }

    private Vendor newVendor(UUID orgId) {
        Vendor v = new Vendor();
        v.setOrganizationId(orgId);
        return v;
    }

    private void applyFields(
            Vendor v, VendorData data) {
        v.setName(data.name().trim());
        v.setPhone(trimOrNull(data.phone()));
        v.setEmail(trimOrNull(data.email()));
        v.setAddressLine1(
                trimOrNull(data.addressLine1()));
        v.setAddressLine2(
                trimOrNull(data.addressLine2()));
        v.setCity(trimOrNull(data.city()));
        v.setStateProvince(
                trimOrNull(data.stateProvince()));
        v.setPostalCode(trimOrNull(data.postalCode()));
        v.setCountry(trimOrNull(data.country()));
        v.setWebsite(trimOrNull(data.website()));
        v.setNotes(trimOrNull(data.notes()));
        syncAltPhones(v, data);
    }

    private void syncAltPhones(
            Vendor v, VendorData data) {
        v.getAltPhones().clear();
        if (data.altPhones() == null) {
            return;
        }
        for (ParsedAltPhone ap : data.altPhones()) {
            if (ap.phone() == null
                    || ap.phone().isBlank()) {
                continue;
            }
            Validation.requireMaxLength(
                    ap.phone(), "Alt phone",
                    VendorFieldLimits.MAX_PHONE);
            Validation.requireMaxLength(
                    ap.label(), "Alt phone label",
                    VendorFieldLimits.MAX_LABEL);
            VendorAltPhone alt = new VendorAltPhone();
            alt.setVendor(v);
            alt.setOrganizationId(v.getOrganizationId());
            alt.setPhone(ap.phone().trim());
            alt.setLabel(trimOrNull(ap.label()));
            v.getAltPhones().add(alt);
        }
    }

    private Vendor findVendor(
            UUID orgId, UUID vendorId) {
        return vendorRepository
                .findByIdAndOrganizationId(
                        vendorId, orgId)
                .orElseThrow(() ->
                        new NotFoundException(
                                "Vendor not found"));
    }

    private String trimOrNull(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }
        return value.trim();
    }
}
