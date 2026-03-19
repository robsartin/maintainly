package solutions.mystuff.domain.service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import solutions.mystuff.domain.model.Item;
import solutions.mystuff.domain.model.PageRequest;
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
    private final PageRequest pageReq =
            new PageRequest(0, 10, "name", "asc");

    @Test
    @DisplayName("should delegate findByOrganization")
    void shouldDelegateFindByOrg() {
        PageResult<Item> expected = new PageResult<>(
                List.of(), 0, 10, false);
        when(itemRepo.findByOrganizationId(
                orgId, pageReq))
                .thenReturn(expected);
        assertEquals(expected,
                service.findByOrganization(
                        orgId, pageReq));
    }

    @Test
    @DisplayName("should delegate searchByOrganization")
    void shouldDelegateSearch() {
        PageResult<Item> expected = new PageResult<>(
                List.of(), 0, 10, false);
        when(itemRepo.searchByOrganizationId(
                orgId, "test", pageReq))
                .thenReturn(expected);
        assertEquals(expected,
                service.searchByOrganization(
                        orgId, "test", pageReq));
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

    @Test
    @DisplayName("should delegate findDistinctCategories")
    void shouldDelegateFindCategories() {
        List<String> expected = List.of("HVAC", "Plumbing");
        when(itemRepo
                .findDistinctCategoriesByOrganizationId(
                        orgId))
                .thenReturn(expected);
        assertEquals(expected,
                service.findDistinctCategories(orgId));
    }

    @Test
    @DisplayName("should delegate"
            + " findByCategoryAndOrganization")
    void shouldDelegateFindByCategory() {
        PageResult<Item> expected = new PageResult<>(
                List.of(), 0, 10, false);
        when(itemRepo.findByCategoryAndOrganizationId(
                orgId, "HVAC", pageReq))
                .thenReturn(expected);
        assertEquals(expected,
                service.findByCategoryAndOrganization(
                        orgId, "HVAC", pageReq));
    }

    @Test
    @DisplayName("should delegate"
            + " searchByCategoryAndOrganization")
    void shouldDelegateSearchByCategory() {
        PageResult<Item> expected = new PageResult<>(
                List.of(), 0, 10, false);
        when(itemRepo.searchByCategoryAndOrganizationId(
                orgId, "test", "HVAC", pageReq))
                .thenReturn(expected);
        assertEquals(expected,
                service.searchByCategoryAndOrganization(
                        orgId, "test", "HVAC", pageReq));
    }

    @Test
    @DisplayName("should dispatch findItems with no filter")
    void shouldDispatchFindItemsNoFilter() {
        PageResult<Item> expected = new PageResult<>(
                List.of(), 0, 10, false);
        when(itemRepo.findByOrganizationId(
                orgId, pageReq))
                .thenReturn(expected);
        assertEquals(expected,
                service.findItems(
                        orgId, null, null, pageReq));
    }

    @Test
    @DisplayName("should dispatch findItems with query")
    void shouldDispatchFindItemsWithQuery() {
        PageResult<Item> expected = new PageResult<>(
                List.of(), 0, 10, false);
        when(itemRepo.searchByOrganizationId(
                orgId, "test", pageReq))
                .thenReturn(expected);
        assertEquals(expected,
                service.findItems(
                        orgId, "test", null, pageReq));
    }

    @Test
    @DisplayName("should dispatch findItems with category")
    void shouldDispatchFindItemsWithCategory() {
        PageResult<Item> expected = new PageResult<>(
                List.of(), 0, 10, false);
        when(itemRepo.findByCategoryAndOrganizationId(
                orgId, "HVAC", pageReq))
                .thenReturn(expected);
        assertEquals(expected,
                service.findItems(
                        orgId, null, "HVAC", pageReq));
    }

    @Test
    @DisplayName("should dispatch findItems with both")
    void shouldDispatchFindItemsWithBoth() {
        PageResult<Item> expected = new PageResult<>(
                List.of(), 0, 10, false);
        when(itemRepo.searchByCategoryAndOrganizationId(
                orgId, "test", "HVAC", pageReq))
                .thenReturn(expected);
        assertEquals(expected,
                service.findItems(
                        orgId, "test", "HVAC", pageReq));
    }

    @Test
    @DisplayName("should treat blank as null in findItems")
    void shouldTreatBlankAsNullInFindItems() {
        PageResult<Item> expected = new PageResult<>(
                List.of(), 0, 10, false);
        when(itemRepo.findByOrganizationId(
                orgId, pageReq))
                .thenReturn(expected);
        assertEquals(expected,
                service.findItems(
                        orgId, "  ", "  ", pageReq));
    }
}
