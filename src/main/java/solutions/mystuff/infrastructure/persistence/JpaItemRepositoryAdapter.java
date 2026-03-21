package solutions.mystuff.infrastructure.persistence;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import solutions.mystuff.domain.model.Item;
import solutions.mystuff.domain.model.PageRequest;
import solutions.mystuff.domain.model.PageResult;
import solutions.mystuff.domain.port.out.ItemRepository;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Repository;

/**
 * Adapts {@link SpringDataItemRepository} to the {@link ItemRepository} port.
 *
 * <div class="mermaid">
 * classDiagram
 *     class JpaItemRepositoryAdapter
 *     class ItemRepository
 *     class SpringDataItemRepository
 *     JpaItemRepositoryAdapter --|> ItemRepository
 *     JpaItemRepositoryAdapter --> SpringDataItemRepository
 * </div>
 *
 * @see ItemRepository
 * @see SpringDataItemRepository
 */
@Repository
public class JpaItemRepositoryAdapter
        implements ItemRepository {

    private final SpringDataItemRepository delegate;

    /** Creates an adapter backed by the given Spring Data repository. */
    public JpaItemRepositoryAdapter(
            SpringDataItemRepository delegate) {
        this.delegate = delegate;
    }

    /** {@inheritDoc} */
    @Override
    public List<Item> findByOrganizationId(
            UUID organizationId) {
        return delegate.findByOrganizationId(
                organizationId);
    }

    /** {@inheritDoc} */
    @Override
    public PageResult<Item> findByOrganizationId(
            UUID organizationId,
            PageRequest pageReq) {
        Slice<Item> s = delegate.findByOrganizationId(
                organizationId, toSpring(pageReq));
        return PageResultConverter.toPageResult(s);
    }

    /** {@inheritDoc} */
    @Override
    public List<Item> searchByOrganizationId(
            UUID organizationId, String query) {
        return delegate.searchByOrganizationId(
                organizationId, query);
    }

    /** {@inheritDoc} */
    @Override
    public PageResult<Item> searchByOrganizationId(
            UUID organizationId, String query,
            PageRequest pageReq) {
        Slice<Item> s = delegate.searchByOrganizationId(
                organizationId, query,
                toSpring(pageReq));
        return PageResultConverter.toPageResult(s);
    }

    /** {@inheritDoc} */
    @Override
    public Optional<Item> findByIdAndOrganizationId(
            UUID id, UUID organizationId) {
        return delegate.findByIdAndOrganizationId(
                id, organizationId);
    }

    /** {@inheritDoc} */
    @Override
    public long countByOrganizationId(
            UUID organizationId) {
        return delegate.countByOrganizationId(
                organizationId);
    }

    /** {@inheritDoc} */
    @Override
    public long countByFacilityId(
            UUID organizationId, UUID facilityId) {
        return delegate.countByFacilityId(
                organizationId, facilityId);
    }

    /** {@inheritDoc} */
    @Override
    public List<String>
            findDistinctCategoriesByOrganizationId(
                    UUID organizationId) {
        return delegate
                .findDistinctCategoriesByOrganizationId(
                        organizationId);
    }

    /** {@inheritDoc} */
    @Override
    public PageResult<Item>
            findByCategoryAndOrganizationId(
                    UUID organizationId, String category,
                    PageRequest pageReq) {
        Slice<Item> s = delegate
                .findByCategoryAndOrganizationId(
                        organizationId, category,
                        toSpring(pageReq));
        return PageResultConverter.toPageResult(s);
    }

    /** {@inheritDoc} */
    @Override
    public PageResult<Item>
            searchByCategoryAndOrganizationId(
                    UUID organizationId, String query,
                    String category,
                    PageRequest pageReq) {
        Slice<Item> s = delegate
                .searchByCategoryAndOrganizationId(
                        organizationId, query, category,
                        toSpring(pageReq));
        return PageResultConverter.toPageResult(s);
    }

    /** {@inheritDoc} */
    @Override
    public Item save(Item item) {
        return delegate.save(item);
    }

    /**
     * Deletes an item via bulk JPQL, bypassing Hibernate
     * entity-level cascade. The database {@code ON DELETE CASCADE}
     * removes child schedules and records in a single statement.
     */
    @Override
    public void deleteByIdAndOrganizationId(
            UUID id, UUID organizationId) {
        delegate.deleteByIdAndOrganizationId(
                id, organizationId);
    }

    /**
     * Bulk-deletes items via JPQL, bypassing Hibernate
     * entity-level cascade. The database
     * {@code ON DELETE CASCADE} removes children.
     */
    @Override
    public void deleteAllByIdsAndOrganizationId(
            List<UUID> ids, UUID organizationId) {
        delegate.deleteAllByIdsAndOrganizationId(
                ids, organizationId);
    }

    /** {@inheritDoc} */
    @Override
    public void updateCategoryByIdsAndOrganizationId(
            List<UUID> ids, UUID organizationId,
            String category) {
        delegate.updateCategoryByIdsAndOrganizationId(
                ids, organizationId, category);
    }

    private static final Set<String> SORTABLE_FIELDS =
            Set.of("name", "location", "category",
                    "manufacturer");

    private org.springframework.data.domain.PageRequest
            toSpring(PageRequest req) {
        String field =
                SORTABLE_FIELDS.contains(req.sort())
                        ? req.sort() : "name";
        Sort.Direction direction =
                "desc".equalsIgnoreCase(req.dir())
                        ? Sort.Direction.DESC
                        : Sort.Direction.ASC;
        return org.springframework.data.domain
                .PageRequest.of(
                        req.page(), req.size(),
                        Sort.by(direction, field));
    }

}
