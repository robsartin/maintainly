package solutions.mystuff.infrastructure.persistence;

import java.util.UUID;

import solutions.mystuff.domain.model.Organization;
import solutions.mystuff.domain.port.out.OrganizationRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA adapter for the {@link OrganizationRepository} port.
 *
 * <div class="mermaid">
 * classDiagram
 *     class JpaOrganizationRepository
 *     class JpaRepository~Organization, UUID~
 *     class OrganizationRepository
 *     JpaOrganizationRepository --|> JpaRepository~Organization, UUID~
 *     JpaOrganizationRepository --|> OrganizationRepository
 * </div>
 *
 * @see OrganizationRepository
 * @see Organization
 */
@Repository
public interface JpaOrganizationRepository
        extends JpaRepository<Organization, UUID>,
        OrganizationRepository {
}
