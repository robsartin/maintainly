package solutions.mystuff.domain.port.out;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import solutions.mystuff.domain.model.Vendor;

public interface VendorRepository {

    List<Vendor> findByOrganizationId(
            UUID organizationId);

    Optional<Vendor> findByIdAndOrganizationId(
            UUID id, UUID organizationId);

    Vendor save(Vendor vendor);
}
