package solutions.mystuff.domain.service;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import solutions.mystuff.domain.model.Facility;
import solutions.mystuff.domain.model.FacilitySummary;
import solutions.mystuff.domain.model.ServiceRecord;
import solutions.mystuff.domain.port.in.DashboardQuery;
import solutions.mystuff.domain.port.out.FacilityRepository;
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
    private final FacilityRepository facilityRepo;

    /** Creates a service backed by the given repositories. */
    public DashboardQueryService(
            ServiceScheduleRepository scheduleRepo,
            ItemRepository itemRepo,
            ServiceRecordRepository recordRepo,
            FacilityRepository facilityRepo) {
        this.scheduleRepo = scheduleRepo;
        this.itemRepo = itemRepo;
        this.recordRepo = recordRepo;
        this.facilityRepo = facilityRepo;
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

    /** {@inheritDoc} */
    @Override
    public long countOverdueByFacility(
            UUID orgId, UUID facilityId,
            LocalDate today) {
        return scheduleRepo
                .countActiveBeforeDateByFacility(
                        orgId, facilityId, today);
    }

    /** {@inheritDoc} */
    @Override
    public long countDueSoonByFacility(
            UUID orgId, UUID facilityId,
            LocalDate from, LocalDate to) {
        return scheduleRepo
                .countActiveBetweenDatesByFacility(
                        orgId, facilityId, from, to);
    }

    /** {@inheritDoc} */
    @Override
    public long countItemsByFacility(
            UUID orgId, UUID facilityId) {
        return itemRepo.countByFacilityId(
                orgId, facilityId);
    }

    /** {@inheritDoc} */
    @Override
    public List<ServiceRecord> findRecentByFacility(
            UUID orgId, UUID facilityId, int limit) {
        return recordRepo.findRecentByFacility(
                orgId, facilityId, limit);
    }

    /** {@inheritDoc} */
    @Override
    public List<FacilitySummary> findFacilitySummaries(
            UUID orgId, LocalDate today) {
        List<Facility> facilities =
                facilityRepo.findByOrganizationId(orgId);
        return facilities.stream()
                .map(f -> new FacilitySummary(
                        f.getId(), f.getName(),
                        itemRepo.countByFacilityId(
                                orgId, f.getId()),
                        scheduleRepo
                            .countActiveBeforeDateByFacility(
                                orgId, f.getId(), today)))
                .toList();
    }
}
