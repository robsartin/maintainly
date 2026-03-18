package solutions.mystuff.domain.service;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import solutions.mystuff.domain.model.ServiceRecord;
import solutions.mystuff.domain.model.UuidV7;
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
    private final DashboardQueryService service =
            new DashboardQueryService(
                    schedRepo, itemRepo, recordRepo);

    private final UUID orgId = UuidV7.generate();

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
}
