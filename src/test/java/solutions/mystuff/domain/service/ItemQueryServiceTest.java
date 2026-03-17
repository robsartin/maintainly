package solutions.mystuff.domain.service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import solutions.mystuff.domain.model.Item;
import solutions.mystuff.domain.model.PageResult;
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
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@DisplayName("ItemQueryService")
class ItemQueryServiceTest {

    private final ItemRepository itemRepo =
            mock(ItemRepository.class);
    private final ServiceRecordRepository recordRepo =
            mock(ServiceRecordRepository.class);
    private final ServiceScheduleRepository schedRepo =
            mock(ServiceScheduleRepository.class);
    private final ItemQueryService service =
            new ItemQueryService(itemRepo, recordRepo,
                    schedRepo);

    private final UUID orgId = UuidV7.generate();

    @Test
    @DisplayName("should delegate findByOrganization")
    void shouldDelegateFindByOrg() {
        PageResult<Item> expected = new PageResult<>(
                List.of(), 0, 10, false);
        when(itemRepo.findByOrganizationId(
                orgId, 0, 10)).thenReturn(expected);
        assertEquals(expected,
                service.findByOrganization(orgId, 0, 10));
    }

    @Test
    @DisplayName("should delegate searchByOrganization")
    void shouldDelegateSearch() {
        PageResult<Item> expected = new PageResult<>(
                List.of(), 0, 10, false);
        when(itemRepo.searchByOrganizationId(
                orgId, "test", 0, 10))
                .thenReturn(expected);
        assertEquals(expected,
                service.searchByOrganization(
                        orgId, "test", 0, 10));
    }

    @Test
    @DisplayName("should delegate findAllByOrganization")
    void shouldDelegateFindAll() {
        when(itemRepo.findByOrganizationId(orgId))
                .thenReturn(List.of());
        assertTrue(service.findAllByOrganization(orgId)
                .isEmpty());
    }

    @Test
    @DisplayName("should delegate findByIdAndOrganization")
    void shouldDelegateFindById() {
        UUID itemId = UuidV7.generate();
        when(itemRepo.findByIdAndOrganizationId(
                itemId, orgId))
                .thenReturn(Optional.empty());
        assertTrue(service.findByIdAndOrganization(
                itemId, orgId).isEmpty());
    }

    @Test
    @DisplayName("should delegate findRecordsByItem")
    void shouldDelegateFindRecords() {
        UUID itemId = UuidV7.generate();
        service.findRecordsByItem(itemId, orgId);
        verify(recordRepo)
                .findByItemIdAndOrganizationId(
                        itemId, orgId);
    }

    @Test
    @DisplayName("should delegate findSchedulesByItem")
    void shouldDelegateFindSchedules() {
        UUID itemId = UuidV7.generate();
        service.findSchedulesByItem(itemId, orgId);
        verify(schedRepo)
                .findByItemIdAndOrganizationId(
                        itemId, orgId);
    }
}
