package solutions.mystuff.infrastructure.persistence;

import solutions.mystuff.domain.model.PageResult;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.Sort;

/**
 * Converts Spring Data {@link Slice} to the domain
 * {@link PageResult}, keeping Spring Data types out of
 * the domain layer.
 */
final class PageResultConverter {

    private PageResultConverter() {
    }

    /** Converts a slice to a page result with sort info. */
    static <T> PageResult<T> toPageResult(
            Slice<T> slice) {
        String sortField = "name";
        String sortDir = "asc";
        Sort sort = slice.getSort();
        Sort.Order order = sort.iterator().hasNext()
                ? sort.iterator().next() : null;
        if (order != null) {
            sortField = order.getProperty();
            sortDir = order.getDirection()
                    == Sort.Direction.ASC ? "asc" : "desc";
        }
        return new PageResult<>(
                slice.getContent(),
                slice.getNumber(),
                slice.getSize(),
                slice.hasNext(),
                sortField,
                sortDir);
    }
}
