package com.robsartin.maintainly.application.web;

import com.robsartin.maintainly.domain.model.PageResult;
import jakarta.servlet.http.HttpServletResponse;

final class LinkHeaderBuilder {

    private LinkHeaderBuilder() {
    }

    static void addLinkHeader(
            HttpServletResponse response,
            String basePath,
            PageResult<?> page, String q) {
        StringBuilder link = new StringBuilder();
        int size = page.size();
        appendLink(link, basePath, 0, size, q, "first");
        appendLink(link, basePath,
                Math.max(0, page.totalPages() - 1),
                size, q, "last");
        if (page.hasPrevious()) {
            appendLink(link, basePath,
                    page.page() - 1, size, q, "prev");
        }
        if (page.hasNext()) {
            appendLink(link, basePath,
                    page.page() + 1, size, q, "next");
        }
        if (!link.isEmpty()) {
            response.addHeader("Link",
                    link.toString());
        }
    }

    private static void appendLink(
            StringBuilder sb, String basePath,
            int page, int size,
            String q, String rel) {
        if (!sb.isEmpty()) {
            sb.append(", ");
        }
        sb.append("<").append(basePath)
                .append("?page=").append(page)
                .append("&size=").append(size);
        if (q != null && !q.isBlank()) {
            sb.append("&q=").append(q);
        }
        sb.append(">; rel=\"").append(rel).append("\"");
    }
}
