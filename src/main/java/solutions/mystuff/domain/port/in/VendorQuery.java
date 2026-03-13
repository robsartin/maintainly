package solutions.mystuff.domain.port.in;

import java.util.List;
import java.util.UUID;

import solutions.mystuff.domain.model.Vendor;

/**
 * Inbound port for read-only vendor queries.
 *
 * @see solutions.mystuff.domain.model.Vendor
 */
public interface VendorQuery {

    /** Find all vendors for an organization. */
    List<Vendor> findAllVendors(UUID orgId);
}
