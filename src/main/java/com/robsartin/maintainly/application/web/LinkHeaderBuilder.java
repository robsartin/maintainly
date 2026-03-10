package com.robsartin.maintainly.application.web;

import com.robsartin.maintainly.domain.model.PageResult;
import jakarta.servlet.http.HttpServletResponse;

final class LinkHeaderBuilder {

    private LinkHeaderBuilder() {
    }

    static void addLinkHeader(
            HttpServletResponse response,
            String prefix,
            PageResult<?> page, String q) {
        StringBuilder link = new StringBuilder();
        String pageParam = prefix + "Page";
        String sizeParam = prefix + "Size";
        int size = page.size();
        appendLink(link, pageParam, 0, sizeParam,
                size, q, "first");
        appendLink(link, pageParam,
                Math.max(0, page.totalPages() - 1),
                sizeParam, size, q, "last");
        if (page.hasPrevious()) {
            appendLink(link, pageParam,
                    page.page() - 1, sizeParam,
                    size, q, "prev");
        }
        if (page.hasNext()) {
            appendLink(link, pageParam,
                    page.page() + 1, sizeParam,
                    size, q, "next");
        }
        if (!link.isEmpty()) {
            response.addHeader("Link",
                    link.toString());
        }
    }

    private static void appendLink(
            StringBuilder sb, String pageParam,
            int page, String sizeParam, int size,
            String q, String rel) {
        if (!sb.isEmpty()) {
            sb.append(", ");
        }
        sb.append("</?").append(pageParam).append("=")
                .append(page).append("&")
                .append(sizeParam).append("=")
                .append(size);
        if (q != null && !q.isBlank()) {
            sb.append("&q=").append(q);
        }
        sb.append(">; rel=\"").append(rel).append("\"");
    }
}
