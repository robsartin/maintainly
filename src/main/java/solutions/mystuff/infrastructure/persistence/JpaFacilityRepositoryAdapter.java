package solutions.mystuff.infrastructure.persistence;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import solutions.mystuff.domain.model.Facility;
import solutions.mystuff.domain.port.out.FacilityRepository;
import org.springframework.stereotype.Repository;

/**
 * Adapts {@link SpringDataFacilityRepository} to the
 * {@link FacilityRepository} port.
 *
 * <div class="mermaid">
 * classDiagram
 *     class JpaFacilityRepositoryAdapter
 *     class FacilityRepository
 *     class SpringDataFacilityRepository
 *     JpaFacilityRepositoryAdapter --|> FacilityRepository
 *     JpaFacilityRepositoryAdapter --> SpringDataFacilityRepository
 * </div>
 *
 * @see FacilityRepository
 * @see SpringDataFacilityRepository
 */
@Repository
public class JpaFacilityRepositoryAdapter
        implements FacilityRepository {

    private final SpringDataFacilityRepository delegate;

    /** Creates an adapter backed by the given repository. */
    public JpaFacilityRepositoryAdapter(
            SpringDataFacilityRepository delegate) {
        this.delegate = delegate;
    }

    /** {@inheritDoc} */
    @Override
    public List<Facility> findByOrganizationId(
            UUID organizationId) {
        return delegate
                .findByOrganizationIdOrderByNameAsc(
                        organizationId);
    }

    /** {@inheritDoc} */
    @Override
    public Optional<Facility> findByIdAndOrganizationId(
            UUID id, UUID organizationId) {
        return delegate.findByIdAndOrganizationId(
                id, organizationId);
    }
}
