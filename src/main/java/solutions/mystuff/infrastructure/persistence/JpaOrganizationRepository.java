package solutions.mystuff.infrastructure.persistence;

import java.util.UUID;

import solutions.mystuff.domain.model.Organization;
import solutions.mystuff.domain.port.out.OrganizationRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface JpaOrganizationRepository
        extends JpaRepository<Organization, UUID>,
        OrganizationRepository {
}
