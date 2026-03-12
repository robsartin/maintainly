package solutions.mystuff.domain.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import solutions.mystuff.domain.model.LogSanitizer;
import solutions.mystuff.domain.model.Vendor;
import solutions.mystuff.domain.model.VendorAltPhone;
import solutions.mystuff.domain.port.in.VendorImportExport;
import solutions.mystuff.domain.port.out.VendorRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Imports and exports vendors using vCard 4.0 format.
 *
 * <p>All imported fields are sanitized via {@link LogSanitizer}
 * and truncated to their database column maximum lengths.
 *
 * <div class="mermaid">
 * sequenceDiagram
 *     Controller->>VendorImportExportService: importVendors(...)
 *     VendorImportExportService->>VCardParser: parse(vcfContent)
 *     VendorImportExportService->>LogSanitizer: sanitize(field)
 *     VendorImportExportService->>VendorRepository: save(vendor)
 * </div>
 *
 * @see VendorImportExport
 * @see VCardSerializer
 * @see VCardParser
 */
@Service
public class VendorImportExportService
        implements VendorImportExport {

    private final VendorRepository vendorRepository;

    public VendorImportExportService(
            VendorRepository vendorRepository) {
        this.vendorRepository = vendorRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public String exportVendor(
            UUID orgId, UUID vendorId) {
        Vendor vendor = vendorRepository
                .findByIdAndOrganizationId(
                        vendorId, orgId)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Vendor not found"));
        return VCardSerializer.serialize(vendor);
    }

    @Override
    @Transactional(readOnly = true)
    public String exportAllVendors(UUID orgId) {
        List<Vendor> vendors =
                vendorRepository
                        .findByOrganizationId(orgId);
        return VCardSerializer.serializeAll(vendors);
    }

    @Override
    public List<Vendor> importVendors(
            UUID orgId, String vcfContent) {
        if (vcfContent == null
                || vcfContent.isBlank()) {
            throw new IllegalArgumentException(
                    "File is empty");
        }
        List<Map<String, Object>> cards =
                VCardParser.parse(vcfContent);
        if (cards.isEmpty()) {
            throw new IllegalArgumentException(
                    "No valid contacts found in file");
        }
        List<Vendor> result = new ArrayList<>();
        for (Map<String, Object> card : cards) {
            Vendor vendor = mapToVendor(orgId, card);
            addAltPhones(orgId, vendor, card);
            result.add(
                    vendorRepository.save(vendor));
        }
        return result;
    }

    @SuppressWarnings("unchecked")
    private void addAltPhones(
            UUID orgId, Vendor vendor,
            Map<String, Object> card) {
        List<Map<String, String>> altList =
                (List<Map<String, String>>)
                        card.get("altPhones");
        if (altList == null) {
            return;
        }
        for (Map<String, String> alt : altList) {
            VendorAltPhone altPhone =
                    new VendorAltPhone();
            altPhone.setPhone(sanitize(
                    alt.get("phone"),
                    VendorFieldLimits.MAX_PHONE));
            altPhone.setLabel(sanitize(
                    alt.get("label"),
                    VendorFieldLimits.MAX_LABEL));
            altPhone.setOrganizationId(orgId);
            altPhone.setVendor(vendor);
            vendor.getAltPhones().add(altPhone);
        }
    }

    private Vendor mapToVendor(
            UUID orgId, Map<String, Object> card) {
        Vendor v = new Vendor();
        v.setOrganizationId(orgId);
        mapContactFields(v, card);
        mapAddressFields(v, card);
        v.setWebsite(sanitize(
                str(card, "website"),
                VendorFieldLimits.MAX_URL));
        v.setNotes(sanitize(str(card, "notes"),
                VendorFieldLimits.MAX_NOTES));
        v.setAltPhones(new ArrayList<>());
        return v;
    }

    private void mapContactFields(
            Vendor v, Map<String, Object> card) {
        v.setName(sanitize(str(card, "name"),
                VendorFieldLimits.MAX_NAME));
        v.setPhone(sanitize(str(card, "phone"),
                VendorFieldLimits.MAX_PHONE));
        v.setEmail(sanitize(str(card, "email"),
                VendorFieldLimits.MAX_EMAIL));
    }

    private void mapAddressFields(
            Vendor v, Map<String, Object> card) {
        v.setAddressLine1(sanitize(
                str(card, "addressLine1"),
                VendorFieldLimits.MAX_ADDR));
        v.setAddressLine2(sanitize(
                str(card, "addressLine2"),
                VendorFieldLimits.MAX_ADDR));
        v.setCity(sanitize(str(card, "city"),
                VendorFieldLimits.MAX_CITY));
        v.setStateProvince(sanitize(
                str(card, "stateProvince"),
                VendorFieldLimits.MAX_STATE));
        v.setPostalCode(sanitize(
                str(card, "postalCode"),
                VendorFieldLimits.MAX_POSTAL));
        v.setCountry(sanitize(
                str(card, "country"),
                VendorFieldLimits.MAX_COUNTRY));
    }

    private String str(
            Map<String, Object> card, String key) {
        Object val = card.get(key);
        return val != null ? val.toString() : null;
    }

    private String sanitize(
            String value, int maxLength) {
        if (value == null) {
            return null;
        }
        String sanitized =
                LogSanitizer.sanitize(value);
        if (sanitized.length() > maxLength) {
            return sanitized.substring(0, maxLength);
        }
        return sanitized;
    }
}
