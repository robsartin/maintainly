package com.robsartin.maintainly.domain.model;

import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DisplayName("PageResult")
class PageResultTest {

    @Test
    @DisplayName("should report hasNext on middle page")
    void shouldReportHasNext() {
        PageResult<String> page = new PageResult<>(
                List.of("a"), 0, 10, 20, 2);
        assertTrue(page.hasNext());
    }

    @Test
    @DisplayName("should report no next on last page")
    void shouldReportNoNext() {
        PageResult<String> page = new PageResult<>(
                List.of("a"), 1, 10, 20, 2);
        assertFalse(page.hasNext());
    }

    @Test
    @DisplayName("should report hasPrevious on second page")
    void shouldReportHasPrevious() {
        PageResult<String> page = new PageResult<>(
                List.of("a"), 1, 10, 20, 2);
        assertTrue(page.hasPrevious());
    }

    @Test
    @DisplayName("should report no previous on first page")
    void shouldReportNoPrevious() {
        PageResult<String> page = new PageResult<>(
                List.of("a"), 0, 10, 20, 2);
        assertFalse(page.hasPrevious());
    }

    @Test
    @DisplayName("should expose record fields")
    void shouldExposeFields() {
        List<String> items = List.of("x", "y");
        PageResult<String> page = new PageResult<>(
                items, 2, 5, 100, 20);
        assertEquals(items, page.content());
        assertEquals(2, page.page());
        assertEquals(5, page.size());
        assertEquals(100, page.totalElements());
        assertEquals(20, page.totalPages());
    }
}
