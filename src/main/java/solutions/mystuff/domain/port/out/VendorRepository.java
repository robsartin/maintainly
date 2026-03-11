package solutions.mystuff.domain.port.out;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import solutions.mystuff.domain.model.Vendor;

/**
 * Outbound port for persisting and retrieving vendors.
 *
 * <pre>{@code
 * classDiagram
 *     class VendorRepository {
 *         <<interface>>
 *         +findByOrganizationId(UUID) List~Vendor~
 *         +findByIdAndOrganizationId(UUID, UUID) Optional~Vendor~
 *         +save(Vendor) Vendor
 *     }
 *     JpaVendorRepository ..|> VendorRepository
 * }</pre>
 *
 * @see solutions.mystuff.domain.model.Vendor
 */
public interface VendorRepository {

    /** Find all vendors belonging to an organization. */
    List<Vendor> findByOrganizationId(
            UUID organizationId);

    /** Find a single vendor by ID scoped to an organization. */
    Optional<Vendor> findByIdAndOrganizationId(
            UUID id, UUID organizationId);

    /** Persist a new or updated vendor. */
    Vendor save(Vendor vendor);
}
