package solutions.mystuff.domain.model;

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
        UUID orgId = UuidV7.generate();
        org.setId(orgId);
        org.setName("Test Org");
        user.setOrganization(org);
        assertNotNull(user.getOrganization());
        assertEquals(orgId, user.getOrganization().getId());
    }

    @Test
    @DisplayName("should report no org when organization is null")
    void shouldReportNoOrgWhenNull() {
        AppUser user = new AppUser(UUID.randomUUID(), "carol");
        assertFalse(user.hasOrganization());
    }

    @Test
    @DisplayName("should report no org when org has null ID")
    void shouldReportNoOrgWhenOrgIdNull() {
        AppUser user = new AppUser(UUID.randomUUID(), "carol");
        user.setOrganization(new Organization());
        assertFalse(user.hasOrganization());
    }

    @Test
    @DisplayName("should report has org when org has ID")
    void shouldReportHasOrgWhenOrgHasId() {
        AppUser user = new AppUser(UUID.randomUUID(), "carol");
        Organization org = new Organization();
        org.setId(UuidV7.generate());
        user.setOrganization(org);
        assertTrue(user.hasOrganization());
    }

    @Test
    @DisplayName("should set and get username")
    void shouldSetUsername() {
        AppUser user = new AppUser();
        user.setUsername("alice");
        assertEquals("alice", user.getUsername());
    }
}
