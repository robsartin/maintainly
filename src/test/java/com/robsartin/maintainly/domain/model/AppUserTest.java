package com.robsartin.maintainly.domain.model;

import java.util.UUID;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DisplayName("AppUser")
class AppUserTest {

    @Test
    @DisplayName("should have default null values")
    void shouldHaveDefaults() {
        AppUser user = new AppUser();
        assertNull(user.getId());
        assertNull(user.getUsername());
        assertNull(user.getOrganization());
    }

    @Test
    @DisplayName("should construct with id and username")
    void shouldConstruct() {
        UUID id = UUID.randomUUID();
        AppUser user = new AppUser(id, "alice");
        assertEquals(id, user.getId());
        assertEquals("alice", user.getUsername());
    }

    @Test
    @DisplayName("should set and get organization")
    void shouldSetOrganization() {
        AppUser user = new AppUser(UUID.randomUUID(), "bob");
        Organization org = new Organization();
        org.setId(1);
        org.setName("Test Org");
        user.setOrganization(org);
        assertNotNull(user.getOrganization());
        assertEquals(1, user.getOrganization().getId());
    }

    @Test
    @DisplayName("should report whether user has organization")
    void shouldReportHasOrganization() {
        AppUser user = new AppUser(UUID.randomUUID(), "carol");
        assertFalse(user.hasOrganization());
        user.setOrganization(new Organization());
        user.getOrganization().setId(1);
        assertTrue(user.hasOrganization());
    }
}
