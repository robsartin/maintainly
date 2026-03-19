package solutions.mystuff.infrastructure.persistence;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import solutions.mystuff.domain.model.Item;
import solutions.mystuff.domain.model.PageResult;
import solutions.mystuff.domain.port.out.ItemRepository;
import org.springframework.data.domain.PageRequest;
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
            UUID organizationId, int page, int size) {
        Slice<Item> s = delegate.findByOrganizationId(
                organizationId, pageOf(page, size));
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
            int page, int size) {
        Slice<Item> s = delegate.searchByOrganizationId(
                organizationId, query, pageOf(page, size));
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
                    int page, int size) {
        Slice<Item> s = delegate
                .findByCategoryAndOrganizationId(
                        organizationId, category,
                        pageOf(page, size));
        return PageResultConverter.toPageResult(s);
    }

    /** {@inheritDoc} */
    @Override
    public PageResult<Item>
            searchByCategoryAndOrganizationId(
                    UUID organizationId, String query,
                    String category, int page, int size) {
        Slice<Item> s = delegate
                .searchByCategoryAndOrganizationId(
                        organizationId, query, category,
                        pageOf(page, size));
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

    private PageRequest pageOf(int page, int size) {
        return PageRequest.of(page, size,
                Sort.by("name").ascending());
    }

}
