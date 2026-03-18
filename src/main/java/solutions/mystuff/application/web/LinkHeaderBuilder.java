package solutions.mystuff.application.web;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

import solutions.mystuff.domain.model.PageResult;
import jakarta.servlet.http.HttpServletResponse;

/**
 * Adds RFC 8288 Link headers to HTTP responses for pagination.
 *
 * @see ItemController
 * @see ScheduleController
 */
public final class LinkHeaderBuilder {

    private LinkHeaderBuilder() {
    }

    /** Builds and sets the Link header with prev/next rels. */
    public static void addLinkHeader(
            HttpServletResponse response,
            String basePath,
            PageResult<?> page, String q) {
        addLinkHeader(response, basePath, page,
                q, null);
    }

    /** Builds Link header including optional category filter. */
    public static void addLinkHeader(
            HttpServletResponse response,
            String basePath,
            PageResult<?> page, String q,
            String category) {
        StringBuilder link = new StringBuilder();
        if (page.hasPrevious()) {
            appendLink(link, basePath,
                    page.page() - 1, page.size(),
                    q, category, "prev");
        }
        if (page.hasNext()) {
            appendLink(link, basePath,
                    page.page() + 1, page.size(),
                    q, category, "next");
        }
        if (!link.isEmpty()) {
            response.addHeader("Link",
                    link.toString());
        }
    }

    private static void appendLink(
            StringBuilder sb, String basePath,
            int page, int size,
            String q, String category, String rel) {
        if (!sb.isEmpty()) {
            sb.append(", ");
        }
        sb.append("<").append(basePath)
                .append("?page=").append(page)
                .append("&size=").append(size);
        if (q != null && !q.isBlank()) {
            sb.append("&q=").append(
                    URLEncoder.encode(q,
                            StandardCharsets.UTF_8));
        }
        if (category != null && !category.isBlank()) {
            sb.append("&category=").append(
                    URLEncoder.encode(category,
                            StandardCharsets.UTF_8));
        }
        sb.append(">; rel=\"").append(rel).append("\"");
    }
}
