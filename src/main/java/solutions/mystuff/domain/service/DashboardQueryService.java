package solutions.mystuff.domain.service;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import solutions.mystuff.domain.model.ServiceRecord;
import solutions.mystuff.domain.port.in.DashboardQuery;
import solutions.mystuff.domain.port.out.ItemRepository;
import solutions.mystuff.domain.port.out
        .ServiceRecordRepository;
import solutions.mystuff.domain.port.out
        .ServiceScheduleRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation
        .Transactional;

/**
 * Aggregates dashboard summary data from multiple repositories.
 *
 * <div class="mermaid">
 * sequenceDiagram
 *     Controller->>DashboardQueryService: countOverdueSchedules(...)
 *     DashboardQueryService->>ServiceScheduleRepository: countActiveBeforeDate(...)
 *     ServiceScheduleRepository-->>Controller: count
 * </div>
 *
 * @see DashboardQuery
 */
@Service
@Transactional(readOnly = true)
public class DashboardQueryService
        implements DashboardQuery {

    private final ServiceScheduleRepository scheduleRepo;
    private final ItemRepository itemRepo;
    private final ServiceRecordRepository recordRepo;

    /** Creates a service backed by the given repositories. */
    public DashboardQueryService(
            ServiceScheduleRepository scheduleRepo,
            ItemRepository itemRepo,
            ServiceRecordRepository recordRepo) {
        this.scheduleRepo = scheduleRepo;
        this.itemRepo = itemRepo;
        this.recordRepo = recordRepo;
    }

    /** {@inheritDoc} */
    @Override
    public long countOverdueSchedules(
            UUID orgId, LocalDate today) {
        return scheduleRepo.countActiveBeforeDate(
                orgId, today);
    }

    /** {@inheritDoc} */
    @Override
    public long countDueSoonSchedules(
            UUID orgId, LocalDate from, LocalDate to) {
        return scheduleRepo.countActiveBetweenDates(
                orgId, from, to);
    }

    /** {@inheritDoc} */
    @Override
    public long countItems(UUID orgId) {
        return itemRepo.countByOrganizationId(orgId);
    }

    /** {@inheritDoc} */
    @Override
    public List<ServiceRecord> findRecentRecords(
            UUID orgId, int limit) {
        return recordRepo.findRecentByOrganizationId(
                orgId, limit);
    }
}
