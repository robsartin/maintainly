package solutions.mystuff.domain.port.in;

import java.util.List;
import java.util.UUID;

import solutions.mystuff.domain.model.Vendor;

/**
 * Inbound port for importing and exporting vendors as vCard 4.0.
 *
 * <div class="mermaid">
 * classDiagram
 *     class VendorImportExport {
 *         +exportVendor(UUID, UUID) String
 *         +exportAllVendors(UUID) String
 *         +importVendors(UUID, String) List~Vendor~
 *     }
 *     VendorImportExportService ..|> VendorImportExport
 * </div>
 *
 * @see solutions.mystuff.domain.model.Vendor
 */
public interface VendorImportExport {

    /** Export a single vendor as a vCard string. */
    String exportVendor(UUID orgId, UUID vendorId);

    /** Export all vendors for an organization. */
    String exportAllVendors(UUID orgId);

    /** Import vendors from vCard content. */
    List<Vendor> importVendors(
            UUID orgId, String vcfContent);
}
