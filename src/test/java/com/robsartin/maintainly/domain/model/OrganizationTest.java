package com.robsartin.maintainly.domain.model;

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
        org.setId(1);
        org.setName("Test Org");
        assertEquals(1, org.getId());
        assertEquals("Test Org", org.getName());
    }
}
