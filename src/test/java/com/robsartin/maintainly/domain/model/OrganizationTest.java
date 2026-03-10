package com.robsartin.maintainly.domain.model;

import java.util.UUID;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

@DisplayName("Organization")
class OrganizationTest {

    @Test
    @DisplayName("should have default null values")
    void shouldHaveDefaults() {
        Organization org = new Organization();
        assertNull(org.getId());
        assertNull(org.getName());
    }

    @Test
    @DisplayName("should set and get fields")
    void shouldSetAndGetFields() {
        Organization org = new Organization();
        UUID id = UuidV7.generate();
        org.setId(id);
        org.setName("Test Org");
        assertEquals(id, org.getId());
        assertEquals("Test Org", org.getName());
    }
}
