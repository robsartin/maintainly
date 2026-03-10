package solutions.mystuff.domain.model;

import java.util.UUID;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

@DisplayName("OrgOwnedEntity")
class OrgOwnedEntityTest {

    static class TestOrgEntity extends OrgOwnedEntity {
    }

    @Test
    @DisplayName("should have null organization ID by default")
    void shouldHaveNullDefault() {
        TestOrgEntity entity = new TestOrgEntity();
        assertNull(entity.getOrganizationId());
    }

    @Test
    @DisplayName("should set and get organization ID")
    void shouldSetAndGetOrganizationId() {
        TestOrgEntity entity = new TestOrgEntity();
        UUID orgId = UuidV7.generate();
        entity.setOrganizationId(orgId);
        assertEquals(orgId, entity.getOrganizationId());
    }

    @Test
    @DisplayName("should inherit BaseEntity behavior")
    void shouldInheritBaseEntity() {
        TestOrgEntity entity = new TestOrgEntity();
        entity.onCreate();
        assertEquals(7, entity.getId().version());
    }
}
