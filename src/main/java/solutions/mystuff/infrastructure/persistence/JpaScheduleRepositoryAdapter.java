package solutions.mystuff.infrastructure.persistence;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import solutions.mystuff.domain.model.PageResult;
import solutions.mystuff.domain.model.ServiceSchedule;
import solutions.mystuff.domain.port.out
        .ServiceScheduleRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Repository;

/**
 * Adapts {@link SpringDataScheduleRepository} to the {@link ServiceScheduleRepository} port.
 *
 * <div class="mermaid">
 * classDiagram
 *     class JpaScheduleRepositoryAdapter
 *     class ServiceScheduleRepository
 *     class SpringDataScheduleRepository
 *     JpaScheduleRepositoryAdapter --|> ServiceScheduleRepository
 *     JpaScheduleRepositoryAdapter --> SpringDataScheduleRepository
 * </div>
 *
 * @see ServiceScheduleRepository
 * @see SpringDataScheduleRepository
 */
@Repository
public class JpaScheduleRepositoryAdapter
        implements ServiceScheduleRepository {

    private final SpringDataScheduleRepository delegate;

    /** Creates an adapter backed by the given Spring Data repository. */
    public JpaScheduleRepositoryAdapter(
            SpringDataScheduleRepository delegate) {
        this.delegate = delegate;
    }

    /** {@inheritDoc} */
    @Override
    public List<ServiceSchedule>
            findByOrganizationIdOrderByNextDueDate(
                    UUID organizationId) {
        return delegate.findByOrgIdOrderByNextDueDate(
                organizationId);
    }

    /** {@inheritDoc} */
    @Override
    public List<ServiceSchedule>
            findActiveByOrganizationId(
                    UUID organizationId) {
        return delegate.findActiveByOrgId(organizationId);
    }

    /** {@inheritDoc} */
    @Override
    public PageResult<ServiceSchedule>
            findActiveByOrganizationId(
                    UUID organizationId,
                    int page, int size) {
        Page<ServiceSchedule> p =
                delegate.findActiveByOrgId(
                        organizationId,
                        PageRequest.of(page, size));
        return toPageResult(p);
    }

    /** {@inheritDoc} */
    @Override
    public Optional<ServiceSchedule>
            findByIdAndOrganizationId(
                    UUID id, UUID organizationId) {
        return delegate.findByIdAndOrganizationId(
                id, organizationId);
    }

    /** {@inheritDoc} */
    @Override
    public List<ServiceSchedule>
            findByItemIdAndOrganizationId(
                    UUID itemId, UUID organizationId) {
        return delegate.findByItemIdAndOrgId(
                itemId, organizationId);
    }

    /** {@inheritDoc} */
    @Override
    public ServiceSchedule save(
            ServiceSchedule schedule) {
        return delegate.save(schedule);
    }

    private <T> PageResult<T> toPageResult(Page<T> p) {
        return new PageResult<>(
                p.getContent(),
                p.getNumber(),
                p.getSize(),
                p.getTotalElements(),
                p.getTotalPages());
    }
}
