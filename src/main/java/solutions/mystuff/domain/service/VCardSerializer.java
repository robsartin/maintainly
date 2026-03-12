package solutions.mystuff.domain.service;

import java.util.List;

import solutions.mystuff.domain.model.Vendor;
import solutions.mystuff.domain.model.VendorAltPhone;

/**
 * Serializes {@link Vendor} entities to vCard 4.0 format (RFC 6350).
 *
 * <p>Produces text/vcard output with CRLF line endings and
 * proper escaping of semicolons, backslashes, and newlines.
 *
 * @see VCardParser
 * @see Vendor
 */
public final class VCardSerializer {

    private static final String CRLF = "\r\n";

    private VCardSerializer() {
    }

    /** Serializes a single vendor to a vCard 4.0 string. */
    public static String serialize(Vendor vendor) {
        StringBuilder sb = new StringBuilder();
        sb.append("BEGIN:VCARD").append(CRLF);
        sb.append("VERSION:4.0").append(CRLF);
        sb.append("FN:").append(escape(vendor.getName()))
                .append(CRLF);
        appendIfPresent(sb, "TEL;TYPE=work",
                vendor.getPhone());
        for (VendorAltPhone alt
                : vendor.getAltPhones()) {
            String label = alt.getLabel() != null
                    && !alt.getLabel().isBlank()
                    ? alt.getLabel() : "work";
            appendIfPresent(sb,
                    "TEL;TYPE=" + escape(label),
                    alt.getPhone());
        }
        appendIfPresent(sb, "EMAIL",
                vendor.getEmail());
        appendAddress(sb, vendor);
        appendIfPresent(sb, "URL",
                vendor.getWebsite());
        appendIfPresent(sb, "NOTE",
                vendor.getNotes());
        sb.append("END:VCARD").append(CRLF);
        return sb.toString();
    }

    /** Serializes a list of vendors to concatenated vCards. */
    public static String serializeAll(
            List<Vendor> vendors) {
        StringBuilder sb = new StringBuilder();
        for (Vendor v : vendors) {
            sb.append(serialize(v));
        }
        return sb.toString();
    }

    private static void appendIfPresent(
            StringBuilder sb, String prop, String value) {
        if (value != null && !value.isBlank()) {
            sb.append(prop).append(':')
                    .append(escape(value)).append(CRLF);
        }
    }

    private static void appendAddress(
            StringBuilder sb, Vendor v) {
        String line1 = safe(v.getAddressLine1());
        String line2 = safe(v.getAddressLine2());
        String city = safe(v.getCity());
        String state = safe(v.getStateProvince());
        String zip = safe(v.getPostalCode());
        String country = safe(v.getCountry());
        if (line1.isEmpty() && line2.isEmpty()
                && city.isEmpty() && state.isEmpty()
                && zip.isEmpty() && country.isEmpty()) {
            return;
        }
        sb.append("ADR;TYPE=work:;")
                .append(escapeAdr(line2)).append(';')
                .append(escapeAdr(line1)).append(';')
                .append(escapeAdr(city)).append(';')
                .append(escapeAdr(state)).append(';')
                .append(escapeAdr(zip)).append(';')
                .append(escapeAdr(country))
                .append(CRLF);
    }

    private static String safe(String value) {
        return value == null ? "" : value;
    }

    static String escape(String value) {
        return value
                .replace("\\", "\\\\")
                .replace(";", "\\;")
                .replace("\n", "\\n")
                .replace("\r", "");
    }

    private static String escapeAdr(String value) {
        return value
                .replace("\\", "\\\\")
                .replace(";", "\\;")
                .replace("\n", "\\n")
                .replace("\r", "");
    }
}
