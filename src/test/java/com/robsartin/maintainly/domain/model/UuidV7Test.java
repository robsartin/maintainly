package com.robsartin.maintainly.domain.model;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DisplayName("UuidV7")
class UuidV7Test {

    @Test
    @DisplayName("should generate version 7 UUID")
    void shouldGenerateVersion7() {
        UUID id = UuidV7.generate();
        assertEquals(7, id.version());
    }

    @Test
    @DisplayName("should generate variant 2 UUID")
    void shouldGenerateVariant2() {
        UUID id = UuidV7.generate();
        assertEquals(2, id.variant());
    }

    @Test
    @DisplayName("should generate unique UUIDs")
    void shouldGenerateUnique() {
        Set<UUID> ids = new HashSet<>();
        for (int i = 0; i < 100; i++) {
            ids.add(UuidV7.generate());
        }
        assertEquals(100, ids.size());
    }

    @Test
    @DisplayName("should embed timestamp in upper bits")
    void shouldEmbedTimestamp() {
        UUID id = UuidV7.generate();
        long msb = id.getMostSignificantBits();
        long timestamp = msb >>> 16;
        long now = System.currentTimeMillis();
        assertTrue(Math.abs(now - timestamp) < 1000);
    }
}
