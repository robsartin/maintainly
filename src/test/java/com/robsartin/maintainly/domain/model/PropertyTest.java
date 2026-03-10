package com.robsartin.maintainly.domain.model;

import java.time.LocalDate;
import java.util.UUID;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

@DisplayName("Property")
class PropertyTest {

    @Test
    @DisplayName("should have default null values")
    void shouldHaveDefaults() {
        Property property = new Property();
        assertNull(property.getId());
        assertNull(property.getName());
        assertNull(property.getAddress());
        assertNull(property.getNextServiceDate());
        assertNull(property.getOrganizationId());
    }

    @Test
    @DisplayName("should set and get all fields")
    void shouldSetAndGetFields() {
        Property p = new Property();
        UUID id = UUID.randomUUID();
        p.setId(id);
        p.setName("123 Main St");
        p.setAddress("123 Main St, Springfield");
        p.setNextServiceDate(LocalDate.of(2026, 4, 1));
        p.setOrganizationId(1);
        assertEquals(id, p.getId());
        assertEquals("123 Main St", p.getName());
        assertEquals("123 Main St, Springfield",
                p.getAddress());
        assertEquals(LocalDate.of(2026, 4, 1),
                p.getNextServiceDate());
        assertEquals(1, p.getOrganizationId());
    }
}
