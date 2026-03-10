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

@Repository
public class JpaScheduleRepositoryAdapter
        implements ServiceScheduleRepository {

    private final SpringDataScheduleRepository delegate;

    public JpaScheduleRepositoryAdapter(
            SpringDataScheduleRepository delegate) {
        this.delegate = delegate;
    }

    @Override
    public List<ServiceSchedule>
            findByOrganizationIdOrderByNextDueDate(
                    UUID organizationId) {
        return delegate.findByOrgIdOrderByNextDueDate(
                organizationId);
    }

    @Override
    public List<ServiceSchedule>
            findActiveByOrganizationId(
                    UUID organizationId) {
        return delegate.findActiveByOrgId(organizationId);
    }

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

    @Override
    public Optional<ServiceSchedule>
            findByIdAndOrganizationId(
                    UUID id, UUID organizationId) {
        return delegate.findByIdAndOrganizationId(
                id, organizationId);
    }

    @Override
    public List<ServiceSchedule>
            findByItemIdAndOrganizationId(
                    UUID itemId, UUID organizationId) {
        return delegate.findByItemIdAndOrgId(
                itemId, organizationId);
    }

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
