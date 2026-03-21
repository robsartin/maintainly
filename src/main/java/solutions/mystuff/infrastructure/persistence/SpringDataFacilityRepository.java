package solutions.mystuff.infrastructure.persistence;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import solutions.mystuff.domain.model.Facility;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Internal Spring Data repository for {@link Facility} persistence.
 *
 * <div class="mermaid">
 * classDiagram
 *     class SpringDataFacilityRepository
 *     class JpaRepository~Facility, UUID~
 *     SpringDataFacilityRepository --|> JpaRepository~Facility, UUID~
 * </div>
 *
 * @see JpaFacilityRepositoryAdapter
 * @see Facility
 */
interface SpringDataFacilityRepository
        extends JpaRepository<Facility, UUID> {

    List<Facility> findByOrganizationIdOrderByNameAsc(
            UUID organizationId);

    Optional<Facility> findByIdAndOrganizationId(
            UUID id, UUID organizationId);

    void deleteByIdAndOrganizationId(
            UUID id, UUID organizationId);
}
