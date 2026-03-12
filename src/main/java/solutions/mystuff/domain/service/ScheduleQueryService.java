package solutions.mystuff.domain.service;

import java.util.List;
import java.util.UUID;

import solutions.mystuff.domain.model.PageResult;
import solutions.mystuff.domain.model.ServiceSchedule;
import solutions.mystuff.domain.port.in.ScheduleQuery;
import solutions.mystuff.domain.port.out
        .ServiceScheduleRepository;
import org.springframework.stereotype.Service;

/**
 * Delegates schedule read queries to the outbound repository port.
 *
 * <div class="mermaid">
 * sequenceDiagram
 *     Controller->>ScheduleQueryService: findActiveByOrganization(...)
 *     ScheduleQueryService->>ServiceScheduleRepository: delegate
 *     ServiceScheduleRepository-->>Controller: result
 * </div>
 *
 * @see ScheduleQuery
 */
@Service
public class ScheduleQueryService
        implements ScheduleQuery {

    private final ServiceScheduleRepository scheduleRepo;

    public ScheduleQueryService(
            ServiceScheduleRepository scheduleRepo) {
        this.scheduleRepo = scheduleRepo;
    }

    /** {@inheritDoc} */
    @Override
    public PageResult<ServiceSchedule>
            findActiveByOrganization(
                    UUID orgId, int page, int size) {
        return scheduleRepo
                .findActiveByOrganizationId(
                        orgId, page, size);
    }

    /** {@inheritDoc} */
    @Override
    public List<ServiceSchedule>
            findAllActiveByOrganization(UUID orgId) {
        return scheduleRepo
                .findActiveByOrganizationId(orgId);
    }
}
