package solutions.mystuff.domain.port.out;

import java.util.Optional;
import java.util.UUID;

import solutions.mystuff.domain.model.Organization;

public interface OrganizationRepository {

    Optional<Organization> findById(UUID id);

    Organization save(Organization organization);
}
