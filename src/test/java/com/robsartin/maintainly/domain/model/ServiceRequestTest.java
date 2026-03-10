package com.robsartin.maintainly.domain.model;

import java.time.LocalDate;
import java.util.UUID;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DisplayName("ServiceRequest")
class ServiceRequestTest {

    @Test
    @DisplayName("should have default values")
    void shouldHaveDefaults() {
        ServiceRequest sr = new ServiceRequest();
        assertNull(sr.getId());
        assertNull(sr.getPropertyId());
        assertNull(sr.getDescription());
        assertNull(sr.getServiceDate());
        assertFalse(sr.isCompleted());
    }

    @Test
    @DisplayName("should set and get all fields")
    void shouldSetAndGetFields() {
        ServiceRequest sr = new ServiceRequest();
        UUID id = UUID.randomUUID();
        UUID propId = UUID.randomUUID();
        sr.setId(id);
        sr.setPropertyId(propId);
        sr.setDescription("Fix HVAC");
        sr.setServiceDate(LocalDate.of(2026, 4, 15));
        sr.setCompleted(true);
        assertEquals(id, sr.getId());
        assertEquals(propId, sr.getPropertyId());
        assertEquals("Fix HVAC", sr.getDescription());
        assertEquals(LocalDate.of(2026, 4, 15),
                sr.getServiceDate());
        assertTrue(sr.isCompleted());
    }
}
