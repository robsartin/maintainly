package solutions.mystuff.domain.model;

import java.util.List;

/**
 * Generic container for a single slice of query results.
 *
 * <p>Uses slice-based pagination (no total count query)
 * for better performance. Carries only whether more
 * results exist beyond this slice.
 *
 * @param <T> the element type contained in this slice
 */
public record PageResult<T>(
        List<T> content,
        int page,
        int size,
        boolean hasNext) {

    /** Return true if a preceding page exists. */
    public boolean hasPrevious() {
        return page > 0;
    }
}
