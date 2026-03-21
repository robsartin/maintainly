package solutions.mystuff.domain.service;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import solutions.mystuff.domain.model.Facility;
import solutions.mystuff.domain.model.ServiceRecord;
import solutions.mystuff.domain.model.UuidV7;
import solutions.mystuff.domain.port.out.FacilityRepository;
import solutions.mystuff.domain.port.out.ItemRepository;
import solutions.mystuff.domain.port.out
        .ServiceRecordRepository;
import solutions.mystuff.domain.port.out
        .ServiceScheduleRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Unit tests for {@link DashboardQueryService}.
 *
 * <div class="mermaid">
 * sequenceDiagram
 *     Test->>DashboardQueryService: query method
 *     DashboardQueryService->>Repository: delegate
 *     Repository-->>Test: result
 * </div>
 */
@DisplayName("DashboardQueryService")
class DashboardQueryServiceTest {

    private final ServiceScheduleRepository schedRepo =
            mock(ServiceScheduleRepository.class);
    private final ItemRepository itemRepo =
            mock(ItemRepository.class);
    private final ServiceRecordRepository recordRepo =
            mock(ServiceRecordRepository.class);
    private final FacilityRepository facilityRepo =
            mock(FacilityRepository.class);
    private final DashboardQueryService service =
            new DashboardQueryService(
                    schedRepo, itemRepo,
                    recordRepo, facilityRepo);

    private final UUID orgId = UuidV7.generate();
    private final UUID facilityId = UuidV7.generate();

    @Test
    @DisplayName("should delegate overdue count")
    void shouldDelegateOverdueCount() {
        LocalDate today = LocalDate.now();
        when(schedRepo.countActiveBeforeDate(
                orgId, today)).thenReturn(3L);
        assertEquals(3L,
                service.countOverdueSchedules(
                        orgId, today));
    }

    @Test
    @DisplayName("should delegate due-soon count")
    void shouldDelegateDueSoonCount() {
        LocalDate from = LocalDate.now();
        LocalDate to = from.plusDays(14);
        when(schedRepo.countActiveBetweenDates(
                orgId, from, to)).thenReturn(5L);
        assertEquals(5L,
                service.countDueSoonSchedules(
                        orgId, from, to));
    }

    @Test
    @DisplayName("should delegate item count")
    void shouldDelegateItemCount() {
        when(itemRepo.countByOrganizationId(orgId))
                .thenReturn(12L);
        assertEquals(12L,
                service.countItems(orgId));
    }

    @Test
    @DisplayName("should delegate recent records query")
    void shouldDelegateRecentRecords() {
        when(recordRepo.findRecentByOrganizationId(
                orgId, 5)).thenReturn(List.of());
        assertTrue(
                service.findRecentRecords(orgId, 5)
                        .isEmpty());
    }

    @Test
    @DisplayName("should delegate overdue count by facility")
    void shouldDelegateOverdueCountWhenFacilityGiven() {
        LocalDate today = LocalDate.now();
        when(schedRepo.countActiveBeforeDateByFacility(
                orgId, facilityId, today))
                .thenReturn(2L);
        assertEquals(2L,
                service.countOverdueByFacility(
                        orgId, facilityId, today));
    }

    @Test
    @DisplayName("should delegate due-soon count by facility")
    void shouldDelegateDueSoonCountWhenFacilityGiven() {
        LocalDate from = LocalDate.now();
        LocalDate to = from.plusDays(14);
        when(schedRepo
                .countActiveBetweenDatesByFacility(
                        orgId, facilityId, from, to))
                .thenReturn(4L);
        assertEquals(4L,
                service.countDueSoonByFacility(
                        orgId, facilityId, from, to));
    }

    @Test
    @DisplayName("should delegate item count by facility")
    void shouldDelegateItemCountWhenFacilityGiven() {
        when(itemRepo.countByFacilityId(
                orgId, facilityId))
                .thenReturn(7L);
        assertEquals(7L,
                service.countItemsByFacility(
                        orgId, facilityId));
    }

    @Test
    @DisplayName("should delegate recent records by facility")
    void shouldDelegateRecentRecordsWhenFacilityGiven() {
        when(recordRepo.findRecentByFacility(
                orgId, facilityId, 5))
                .thenReturn(List.of());
        assertTrue(
                service.findRecentByFacility(
                        orgId, facilityId, 5).isEmpty());
    }

    @Test
    @DisplayName("should build facility summaries")
    void shouldBuildFacilitySummaries() {
        LocalDate today = LocalDate.now();
        Facility f = new Facility();
        f.setId(facilityId);
        f.setName("Building A");
        when(facilityRepo.findByOrganizationId(orgId))
                .thenReturn(List.of(f));
        when(itemRepo.countByFacilityId(
                orgId, facilityId)).thenReturn(10L);
        when(schedRepo.countActiveBeforeDateByFacility(
                orgId, facilityId, today))
                .thenReturn(1L);
        var summaries =
                service.findFacilitySummaries(
                        orgId, today);
        assertEquals(1, summaries.size());
        assertEquals("Building A",
                summaries.get(0).facilityName());
        assertEquals(10L,
                summaries.get(0).itemCount());
        assertEquals(1L,
                summaries.get(0).overdueCount());
    }
}
