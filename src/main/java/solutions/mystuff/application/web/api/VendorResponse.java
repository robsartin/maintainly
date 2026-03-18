package solutions.mystuff.application.web.api;

import java.util.List;
import java.util.UUID;

import solutions.mystuff.domain.model.Vendor;

/**
 * JSON response DTO for a vendor.
 *
 * <div class="mermaid">
 * classDiagram
 *     class VendorResponse {
 *         UUID id
 *         String name
 *         String phone
 *         String email
 *     }
 *     VendorResponse ..&gt; Vendor : from
 * </div>
 *
 * @see VendorApiController
 */
public record VendorResponse(
        UUID id,
        String name,
        String phone,
        String email,
        String city,
        String stateProvince,
        String website,
        List<AltPhone> altPhones) {

    /** Alt phone sub-record. */
    public record AltPhone(
            String phone, String label) {
    }

    /** Creates a response DTO from a domain Vendor. */
    public static VendorResponse from(Vendor v) {
        List<AltPhone> phones = v.getAltPhones()
                .stream()
                .map(ap -> new AltPhone(
                        ap.getPhone(), ap.getLabel()))
                .toList();
        return new VendorResponse(
                v.getId(),
                v.getName(),
                v.getPhone(),
                v.getEmail(),
                v.getCity(),
                v.getStateProvince(),
                v.getWebsite(),
                phones);
    }
}
