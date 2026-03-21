package solutions.mystuff.domain.model;

import java.time.Instant;
import java.util.UUID;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("AuditEntry")
class AuditEntryTest {

    @Test
    @DisplayName("should assign id and timestamp on"
            + " create")
    void shouldAssignIdAndTimestampWhenCreated() {
        AuditEntry entry = new AuditEntry();
        entry.onCreate();
        assertThat(entry.getId()).isNotNull();
        assertThat(entry.getTimestamp()).isNotNull();
    }

    @Test
    @DisplayName("should not overwrite existing id")
    void shouldNotOverwriteExistingId() {
        UUID fixed = UuidV7.generate();
        AuditEntry entry = new AuditEntry();
        entry.setId(fixed);
        entry.onCreate();
        assertThat(entry.getId()).isEqualTo(fixed);
    }

    @Test
    @DisplayName("should not overwrite existing"
            + " timestamp")
    void shouldNotOverwriteExistingTimestamp() {
        Instant fixed = Instant.parse(
                "2026-01-01T00:00:00Z");
        AuditEntry entry = new AuditEntry();
        entry.setTimestamp(fixed);
        entry.onCreate();
        assertThat(entry.getTimestamp())
                .isEqualTo(fixed);
    }

    @Test
    @DisplayName("should store all fields")
    void shouldStoreAllFields() {
        UUID orgId = UuidV7.generate();
        UUID entityId = UuidV7.generate();
        AuditEntry entry = new AuditEntry();
        entry.setOrganizationId(orgId);
        entry.setUsername("dev");
        entry.setEntityType("Item");
        entry.setEntityId(entityId);
        entry.setEntityName("Furnace");
        entry.setAction(AuditAction.CREATE);
        entry.setDetails("Created item");
        assertThat(entry.getOrganizationId())
                .isEqualTo(orgId);
        assertThat(entry.getUsername())
                .isEqualTo("dev");
        assertThat(entry.getEntityType())
                .isEqualTo("Item");
        assertThat(entry.getEntityId())
                .isEqualTo(entityId);
        assertThat(entry.getEntityName())
                .isEqualTo("Furnace");
        assertThat(entry.getAction())
                .isEqualTo(AuditAction.CREATE);
        assertThat(entry.getDetails())
                .isEqualTo("Created item");
    }
}
