package solutions.mystuff.domain.service;

import java.util.List;
import java.util.UUID;

import solutions.mystuff.domain.model.PageResult;
import solutions.mystuff.domain.model.ServiceSchedule;
import solutions.mystuff.domain.model.UuidV7;
import solutions.mystuff.domain.port.out
        .ServiceScheduleRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@DisplayName("ScheduleQueryService")
class ScheduleQueryServiceTest {

    private final ServiceScheduleRepository schedRepo =
            mock(ServiceScheduleRepository.class);
    private final ScheduleQueryService service =
            new ScheduleQueryService(schedRepo);

    private final UUID orgId = UuidV7.generate();

    @Test
    @DisplayName("should delegate paginated active query")
    void shouldDelegatePaginated() {
        PageResult<ServiceSchedule> expected =
                new PageResult<>(List.of(), 0, 10, false);
        when(schedRepo.findActiveByOrganizationId(
                orgId, 0, 10)).thenReturn(expected);
        assertEquals(expected,
                service.findActiveByOrganization(
                        orgId, 0, 10));
    }

    @Test
    @DisplayName("should delegate non-paginated active query")
    void shouldDelegateNonPaginated() {
        when(schedRepo.findActiveByOrganizationId(orgId))
                .thenReturn(List.of());
        assertTrue(
                service.findAllActiveByOrganization(orgId)
                        .isEmpty());
    }
}
