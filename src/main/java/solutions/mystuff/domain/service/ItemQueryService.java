package solutions.mystuff.domain.service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import solutions.mystuff.domain.model.Item;
import solutions.mystuff.domain.model.PageResult;
import solutions.mystuff.domain.model.ServiceRecord;
import solutions.mystuff.domain.model.ServiceSchedule;
import solutions.mystuff.domain.port.in.ItemQuery;
import solutions.mystuff.domain.port.out.ItemRepository;
import solutions.mystuff.domain.port.out
        .ServiceRecordRepository;
import solutions.mystuff.domain.port.out
        .ServiceScheduleRepository;
import org.springframework.stereotype.Service;

/**
 * Delegates item read queries to outbound repository ports.
 *
 * <div class="mermaid">
 * sequenceDiagram
 *     Controller->>ItemQueryService: findByOrganization
 *     ItemQueryService->>ItemRepository: findByOrganizationId
 *     ItemRepository-->>ItemQueryService: PageResult
 *     ItemQueryService-->>Controller: PageResult
 * </div>
 *
 * @see ItemQuery
 */
@Service
public class ItemQueryService implements ItemQuery {

    private final ItemRepository itemRepo;
    private final ServiceRecordRepository recordRepo;
    private final ServiceScheduleRepository scheduleRepo;

    public ItemQueryService(
            ItemRepository itemRepo,
            ServiceRecordRepository recordRepo,
            ServiceScheduleRepository scheduleRepo) {
        this.itemRepo = itemRepo;
        this.recordRepo = recordRepo;
        this.scheduleRepo = scheduleRepo;
    }

    /** {@inheritDoc} */
    @Override
    public PageResult<Item> findByOrganization(
            UUID orgId, int page, int size) {
        return itemRepo.findByOrganizationId(
                orgId, page, size);
    }

    /** {@inheritDoc} */
    @Override
    public PageResult<Item> searchByOrganization(
            UUID orgId, String query,
            int page, int size) {
        return itemRepo.searchByOrganizationId(
                orgId, query, page, size);
    }

    /** {@inheritDoc} */
    @Override
    public PageResult<Item>
            findByCategoryAndOrganization(
                    UUID orgId, String category,
                    int page, int size) {
        return itemRepo
                .findByCategoryAndOrganizationId(
                        orgId, category, page, size);
    }

    /** {@inheritDoc} */
    @Override
    public PageResult<Item>
            searchByCategoryAndOrganization(
                    UUID orgId, String query,
                    String category,
                    int page, int size) {
        return itemRepo
                .searchByCategoryAndOrganizationId(
                        orgId, query, category,
                        page, size);
    }

    /** {@inheritDoc} */
    @Override
    public List<String> findDistinctCategories(
            UUID orgId) {
        return itemRepo
                .findDistinctCategoriesByOrganizationId(
                        orgId);
    }

    /** {@inheritDoc} */
    @Override
    public List<Item> findAllByOrganization(UUID orgId) {
        return itemRepo.findByOrganizationId(orgId);
    }

    /** {@inheritDoc} */
    @Override
    public Optional<Item> findByIdAndOrganization(
            UUID itemId, UUID orgId) {
        return itemRepo.findByIdAndOrganizationId(
                itemId, orgId);
    }

    /** {@inheritDoc} */
    @Override
    public List<ServiceRecord> findRecordsByItem(
            UUID itemId, UUID orgId) {
        return recordRepo.findByItemIdAndOrganizationId(
                itemId, orgId);
    }

    /** {@inheritDoc} */
    @Override
    public List<ServiceSchedule> findSchedulesByItem(
            UUID itemId, UUID orgId) {
        return scheduleRepo
                .findByItemIdAndOrganizationId(
                        itemId, orgId);
    }
}
