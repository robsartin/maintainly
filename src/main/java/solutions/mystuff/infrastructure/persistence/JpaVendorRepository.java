package solutions.mystuff.infrastructure.persistence;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import solutions.mystuff.domain.model.Vendor;
import solutions.mystuff.domain.port.out.VendorRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA adapter for the {@link VendorRepository} port.
 *
 * <pre>{@code
 * classDiagram
 *     class JpaVendorRepository
 *     class JpaRepository~Vendor, UUID~
 *     class VendorRepository
 *     JpaVendorRepository --|> JpaRepository~Vendor, UUID~
 *     JpaVendorRepository --|> VendorRepository
 * }</pre>
 *
 * @see VendorRepository
 * @see Vendor
 */
@Repository
public interface JpaVendorRepository
        extends JpaRepository<Vendor, UUID>,
        VendorRepository {

    @Override
    List<Vendor> findByOrganizationId(
            UUID organizationId);

    @Override
    Optional<Vendor> findByIdAndOrganizationId(
            UUID id, UUID organizationId);
}
