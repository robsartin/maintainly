package solutions.mystuff.domain.model;

import java.util.UUID;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

@DisplayName("BaseEntity")
class BaseEntityTest {

    static class TestEntity extends BaseEntity {
    }

    @Test
    @DisplayName("should have null defaults")
    void shouldHaveNullDefaults() {
        TestEntity entity = new TestEntity();
        assertNull(entity.getId());
        assertNull(entity.getCreatedAt());
        assertNull(entity.getUpdatedAt());
    }

    @Test
    @DisplayName("should generate UUID V7 on create")
    void shouldGenerateIdOnCreate() {
        TestEntity entity = new TestEntity();
        entity.onCreate();
        assertNotNull(entity.getId());
        assertEquals(7, entity.getId().version());
    }

    @Test
    @DisplayName("should preserve existing id on create")
    void shouldPreserveExistingId() {
        TestEntity entity = new TestEntity();
        UUID existing = UuidV7.generate();
        entity.setId(existing);
        entity.onCreate();
        assertEquals(existing, entity.getId());
    }

    @Test
    @DisplayName("should set timestamps on create")
    void shouldSetTimestampsOnCreate() {
        TestEntity entity = new TestEntity();
        entity.onCreate();
        assertNotNull(entity.getCreatedAt());
        assertNotNull(entity.getUpdatedAt());
        assertEquals(entity.getCreatedAt(),
                entity.getUpdatedAt());
    }

    @Test
    @DisplayName("should update timestamp on update")
    void shouldUpdateTimestamp() {
        TestEntity entity = new TestEntity();
        entity.onCreate();
        var created = entity.getUpdatedAt();
        entity.onUpdate();
        assertNotNull(entity.getUpdatedAt());
    }
}
