package solutions.mystuff.application.web.api;

import java.util.List;
import java.util.function.Function;

import solutions.mystuff.domain.model.PageResult;

/**
 * JSON-safe pagination wrapper that maps domain entities
 * to response DTOs.
 *
 * <div class="mermaid">
 * classDiagram
 *     class PageResponse {
 *         List content
 *         int page
 *         int size
 *         boolean hasNext
 *     }
 * </div>
 *
 * @param <T> the DTO element type
 * @see PageResult
 */
public record PageResponse<T>(
        List<T> content,
        int page,
        int size,
        boolean hasNext) {

    /** Maps a PageResult to a PageResponse using a mapper. */
    public static <S, T> PageResponse<T> from(
            PageResult<S> source,
            Function<S, T> mapper) {
        List<T> mapped = source.content().stream()
                .map(mapper)
                .toList();
        return new PageResponse<>(
                mapped,
                source.page(),
                source.size(),
                source.hasNext());
    }
}
