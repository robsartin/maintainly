package solutions.mystuff.infrastructure.persistence;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import solutions.mystuff.domain.model.Item;
import solutions.mystuff.domain.model.PageResult;
import solutions.mystuff.domain.port.out.ItemRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Repository;

@Repository
public class JpaItemRepositoryAdapter
        implements ItemRepository {

    private final SpringDataItemRepository delegate;

    public JpaItemRepositoryAdapter(
            SpringDataItemRepository delegate) {
        this.delegate = delegate;
    }

    @Override
    public List<Item> findByOrganizationId(
            UUID organizationId) {
        return delegate.findByOrganizationId(
                organizationId);
    }

    @Override
    public PageResult<Item> findByOrganizationId(
            UUID organizationId, int page, int size) {
        Page<Item> p = delegate.findByOrganizationId(
                organizationId, pageOf(page, size));
        return toPageResult(p);
    }

    @Override
    public List<Item> searchByOrganizationId(
            UUID organizationId, String query) {
        return delegate.searchByOrganizationId(
                organizationId, query);
    }

    @Override
    public PageResult<Item> searchByOrganizationId(
            UUID organizationId, String query,
            int page, int size) {
        Page<Item> p = delegate.searchByOrganizationId(
                organizationId, query, pageOf(page, size));
        return toPageResult(p);
    }

    @Override
    public Optional<Item> findByIdAndOrganizationId(
            UUID id, UUID organizationId) {
        return delegate.findByIdAndOrganizationId(
                id, organizationId);
    }

    @Override
    public Item save(Item item) {
        return delegate.save(item);
    }

    private PageRequest pageOf(int page, int size) {
        return PageRequest.of(page, size,
                Sort.by("name").ascending());
    }

    private <T> PageResult<T> toPageResult(Page<T> p) {
        return new PageResult<>(
                p.getContent(),
                p.getNumber(),
                p.getSize(),
                p.getTotalElements(),
                p.getTotalPages());
    }
}
