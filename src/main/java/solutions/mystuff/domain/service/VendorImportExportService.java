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

    private static final int MAX_NAME = 200;
    private static final int MAX_PHONE = 50;
    private static final int MAX_EMAIL = 320;
    private static final int MAX_ADDR = 200;
    private static final int MAX_CITY = 100;
    private static final int MAX_STATE = 100;
    private static final int MAX_POSTAL = 30;
    private static final int MAX_COUNTRY = 100;
    private static final int MAX_URL = 2000;
    private static final int MAX_NOTES = 2000;
    private static final int MAX_LABEL = 50;

    private final VendorRepository vendorRepository;

    public VendorImportExportService(
            VendorRepository vendorRepository) {
        this.vendorRepository = vendorRepository;
    }

    @Override
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
                    alt.get("phone"), MAX_PHONE));
            altPhone.setLabel(sanitize(
                    alt.get("label"), MAX_LABEL));
            altPhone.setOrganizationId(orgId);
            altPhone.setVendor(vendor);
            vendor.getAltPhones().add(altPhone);
        }
    }

    private Vendor mapToVendor(
            UUID orgId, Map<String, Object> card) {
        Vendor v = new Vendor();
        v.setOrganizationId(orgId);
        v.setName(sanitize(
                str(card, "name"), MAX_NAME));
        v.setPhone(sanitize(
                str(card, "phone"), MAX_PHONE));
        v.setEmail(sanitize(
                str(card, "email"), MAX_EMAIL));
        v.setAddressLine1(sanitize(
                str(card, "addressLine1"), MAX_ADDR));
        v.setAddressLine2(sanitize(
                str(card, "addressLine2"), MAX_ADDR));
        v.setCity(sanitize(
                str(card, "city"), MAX_CITY));
        v.setStateProvince(sanitize(
                str(card, "stateProvince"),
                MAX_STATE));
        v.setPostalCode(sanitize(
                str(card, "postalCode"),
                MAX_POSTAL));
        v.setCountry(sanitize(
                str(card, "country"), MAX_COUNTRY));
        v.setWebsite(sanitize(
                str(card, "website"), MAX_URL));
        v.setNotes(sanitize(
                str(card, "notes"), MAX_NOTES));
        v.setAltPhones(new ArrayList<>());
        return v;
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
