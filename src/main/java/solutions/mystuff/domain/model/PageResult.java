package solutions.mystuff.domain.model;

import java.util.List;

/**
 * Generic container for a single page of query results.
 *
 * <p>Carries the content list together with pagination metadata
 * so that callers can render paging controls.
 *
 * @param <T> the element type contained in this page
 */
public record PageResult<T>(
        List<T> content,
        int page,
        int size,
        long totalElements,
        int totalPages) {

    /** Return true if a subsequent page exists. */
    public boolean hasNext() {
        return page < totalPages - 1;
    }

    /** Return true if a preceding page exists. */
    public boolean hasPrevious() {
        return page > 0;
    }
}
