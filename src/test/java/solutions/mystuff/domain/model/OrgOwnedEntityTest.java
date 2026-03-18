package solutions.mystuff.domain.model;

import java.util.UUID;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

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
    @DisplayName("should return true when belongsTo matches")
    void shouldReturnTrueWhenBelongsToMatches() {
        TestOrgEntity entity = new TestOrgEntity();
        UUID orgId = UuidV7.generate();
        entity.setOrganizationId(orgId);
        assertTrue(entity.belongsTo(orgId));
    }

    @Test
    @DisplayName("should return false when belongsTo mismatches")
    void shouldReturnFalseWhenBelongsToMismatches() {
        TestOrgEntity entity = new TestOrgEntity();
        entity.setOrganizationId(UuidV7.generate());
        assertFalse(entity.belongsTo(UuidV7.generate()));
    }

    @Test
    @DisplayName("should return false when org ID is null")
    void shouldReturnFalseWhenOrgIdIsNull() {
        TestOrgEntity entity = new TestOrgEntity();
        assertFalse(entity.belongsTo(UuidV7.generate()));
    }

    @Test
    @DisplayName("should inherit BaseEntity behavior")
    void shouldInheritBaseEntity() {
        TestOrgEntity entity = new TestOrgEntity();
        entity.onCreate();
        assertEquals(7, entity.getId().version());
    }
}
