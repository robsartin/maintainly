package solutions.mystuff.infrastructure.persistence;

import solutions.mystuff.domain.model.PageResult;
import org.springframework.data.domain.Slice;

/**
 * Converts Spring Data {@link Slice} to the domain
 * {@link PageResult}, keeping Spring Data types out of
 * the domain layer.
 */
final class PageResultConverter {

    private PageResultConverter() {
    }

    static <T> PageResult<T> toPageResult(
            Slice<T> slice) {
        return new PageResult<>(
                slice.getContent(),
                slice.getNumber(),
                slice.getSize(),
                slice.hasNext());
    }
}
