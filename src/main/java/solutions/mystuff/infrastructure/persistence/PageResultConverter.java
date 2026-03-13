package solutions.mystuff.infrastructure.persistence;

import solutions.mystuff.domain.model.PageResult;
import org.springframework.data.domain.Page;

/**
 * Converts Spring Data {@link Page} to the domain
 * {@link PageResult}, keeping Spring Data types out of
 * the domain layer.
 */
final class PageResultConverter {

    private PageResultConverter() {
    }

    static <T> PageResult<T> toPageResult(Page<T> page) {
        return new PageResult<>(
                page.getContent(),
                page.getNumber(),
                page.getSize(),
                page.getTotalElements(),
                page.getTotalPages());
    }
}
