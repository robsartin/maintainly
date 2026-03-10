package com.robsartin.maintainly.domain.model;

import java.util.ArrayList;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

@DisplayName("Vendor")
class VendorTest {

    @Test
    @DisplayName("should have null defaults")
    void shouldHaveNullDefaults() {
        Vendor v = new Vendor();
        assertNull(v.getOrganizationId());
        assertNull(v.getName());
        assertNull(v.getPhone());
        assertNull(v.getEmail());
        assertNull(v.getAddressLine1());
        assertNull(v.getAddressLine2());
        assertNull(v.getCity());
        assertNull(v.getStateProvince());
        assertNull(v.getPostalCode());
        assertNull(v.getCountry());
        assertNotNull(v.getAltPhones());
    }

    @Test
    @DisplayName("should set and get all fields")
    void shouldSetAndGetFields() {
        Vendor v = new Vendor();
        var orgId = UuidV7.generate();
        v.setOrganizationId(orgId);
        v.setName("ABC Corp");
        v.setPhone("555-0100");
        v.setEmail("info@abc.com");
        v.setAddressLine1("123 Main");
        v.setAddressLine2("Suite 100");
        v.setCity("Springfield");
        v.setStateProvince("IL");
        v.setPostalCode("62701");
        v.setCountry("US");
        v.setAltPhones(new ArrayList<>());
        assertEquals(orgId, v.getOrganizationId());
        assertEquals("ABC Corp", v.getName());
        assertEquals("555-0100", v.getPhone());
        assertEquals("info@abc.com", v.getEmail());
        assertEquals("123 Main", v.getAddressLine1());
        assertEquals("Suite 100", v.getAddressLine2());
        assertEquals("Springfield", v.getCity());
        assertEquals("IL", v.getStateProvince());
        assertEquals("62701", v.getPostalCode());
        assertEquals("US", v.getCountry());
        assertNotNull(v.getAltPhones());
    }
}
