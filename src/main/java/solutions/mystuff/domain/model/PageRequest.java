package solutions.mystuff.domain.model;

/**
 * Domain-level pagination parameters.
 *
 * <p>Bundles page index, page size, sort field, and sort
 * direction into a single value object, replacing the
 * trailing {@code int page, int size, String sort,
 * String dir} parameter lists on paginated query methods.
 *
 * <div class="mermaid">
 * classDiagram
 *     class PageRequest {
 *         +int page
 *         +int size
 *         +String sort
 *         +String dir
 *     }
 *     PageRequest ..> PageResult : used by queries
 * </div>
 *
 * @param page zero-based page index
 * @param size number of items per page
 * @param sort field to sort by (e.g. "name")
 * @param dir  sort direction ("asc" or "desc")
 */
public record PageRequest(
        int page,
        int size,
        String sort,
        String dir) {

    /** Default sort field. */
    public static final String DEFAULT_SORT = "name";

    /** Default sort direction. */
    public static final String DEFAULT_DIR = "asc";

    /**
     * Creates a page request with default sort.
     *
     * @param page zero-based page index
     * @param size number of items per page
     */
    public PageRequest(int page, int size) {
        this(page, size, DEFAULT_SORT, DEFAULT_DIR);
    }
}
