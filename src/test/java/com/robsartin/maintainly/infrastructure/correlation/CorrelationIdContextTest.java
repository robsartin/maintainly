package com.robsartin.maintainly.infrastructure.correlation;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

@DisplayName("CorrelationIdContext")
class CorrelationIdContextTest {

    @AfterEach
    void cleanup() {
        CorrelationIdContext.clear();
    }

    @Test
    @DisplayName("should return null when no ID set")
    void shouldReturnNullWhenEmpty() {
        assertNull(CorrelationIdContext.getId());
    }

    @Test
    @DisplayName("should store and retrieve ID")
    void shouldStoreAndRetrieveId() {
        CorrelationIdContext.setId("test-id-123");
        assertEquals("test-id-123",
                CorrelationIdContext.getId());
    }

    @Test
    @DisplayName("should clear stored ID")
    void shouldClearId() {
        CorrelationIdContext.setId("test-id-456");
        CorrelationIdContext.clear();
        assertNull(CorrelationIdContext.getId());
    }
}
