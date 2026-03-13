package solutions.mystuff.domain.service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import solutions.mystuff.domain.model.LogSanitizer;
import solutions.mystuff.domain.model.ParsedAltPhone;
import solutions.mystuff.domain.model.ParsedVCard;
import solutions.mystuff.domain.model.Vendor;
import solutions.mystuff.domain.model.VendorAltPhone;
import solutions.mystuff.domain.port.in.VendorImportExport;
import solutions.mystuff.domain.port.out.VendorRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Imports and exports vendors using vCard 4.0 format.
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
        List<ParsedVCard> cards =
                VCardParser.parse(vcfContent);
        if (cards.isEmpty()) {
            throw new IllegalArgumentException(
                    "No valid contacts found in file");
        }
        List<Vendor> result = new ArrayList<>();
        for (ParsedVCard card : cards) {
            Vendor vendor = mapToVendor(orgId, card);
            addAltPhones(orgId, vendor, card);
            result.add(
                    vendorRepository.save(vendor));
        }
        return result;
    }

    private void addAltPhones(
            UUID orgId, Vendor vendor,
            ParsedVCard card) {
        if (card.altPhones() == null) {
            return;
        }
        for (ParsedAltPhone alt : card.altPhones()) {
            VendorAltPhone altPhone =
                    new VendorAltPhone();
            altPhone.setPhone(sanitize(
                    alt.phone(),
                    VendorFieldLimits.MAX_PHONE));
            altPhone.setLabel(sanitize(
                    alt.label(),
                    VendorFieldLimits.MAX_LABEL));
            altPhone.setOrganizationId(orgId);
            altPhone.setVendor(vendor);
            vendor.getAltPhones().add(altPhone);
        }
    }

    private Vendor mapToVendor(
            UUID orgId, ParsedVCard card) {
        Vendor v = new Vendor();
        v.setOrganizationId(orgId);
        setVendorFields(v, card);
        v.setAltPhones(new ArrayList<>());
        return v;
    }

    private void setVendorFields(
            Vendor v, ParsedVCard card) {
        v.setName(sanitize(card.name(),
                VendorFieldLimits.MAX_NAME));
        v.setPhone(sanitize(card.phone(),
                VendorFieldLimits.MAX_PHONE));
        v.setEmail(sanitize(card.email(),
                VendorFieldLimits.MAX_EMAIL));
        v.setAddressLine1(sanitize(
                card.addressLine1(),
                VendorFieldLimits.MAX_ADDR));
        v.setAddressLine2(sanitize(
                card.addressLine2(),
                VendorFieldLimits.MAX_ADDR));
        v.setCity(sanitize(card.city(),
                VendorFieldLimits.MAX_CITY));
        v.setStateProvince(sanitize(
                card.stateProvince(),
                VendorFieldLimits.MAX_STATE));
        v.setPostalCode(sanitize(
                card.postalCode(),
                VendorFieldLimits.MAX_POSTAL));
        v.setCountry(sanitize(card.country(),
                VendorFieldLimits.MAX_COUNTRY));
        v.setWebsite(sanitize(card.website(),
                VendorFieldLimits.MAX_URL));
        v.setNotes(sanitize(card.notes(),
                VendorFieldLimits.MAX_NOTES));
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
