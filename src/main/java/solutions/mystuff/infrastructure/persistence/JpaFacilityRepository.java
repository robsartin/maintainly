package solutions.mystuff.infrastructure.persistence;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import solutions.mystuff.domain.model.Facility;
import solutions.mystuff.domain.port.out.FacilityRepository;
import org.springframework.data.jpa.repository
        .JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA adapter for the
 * {@link FacilityRepository} port.
 *
 * <div class="mermaid">
 * classDiagram
 *     class JpaFacilityRepository
 *     class JpaRepository~Facility, UUID~
 *     class FacilityRepository
 *     JpaFacilityRepository --|&gt; JpaRepository
 *     JpaFacilityRepository --|&gt; FacilityRepository
 * </div>
 *
 * @see FacilityRepository
 * @see Facility
 */
@Repository
public interface JpaFacilityRepository
        extends JpaRepository<Facility, UUID>,
        FacilityRepository {

    @Override
    List<Facility> findByOrganizationId(
            UUID organizationId);

    @Override
    Optional<Facility> findByIdAndOrganizationId(
            UUID id, UUID organizationId);

    @Override
    void deleteByIdAndOrganizationId(
            UUID id, UUID organizationId);
}
