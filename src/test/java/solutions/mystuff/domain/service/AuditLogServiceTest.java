package solutions.mystuff.domain.service;

import java.util.List;
import java.util.UUID;

import solutions.mystuff.domain.model.AuditAction;
import solutions.mystuff.domain.model.AuditEntry;
import solutions.mystuff.domain.model.UuidV7;
import solutions.mystuff.domain.port.out.AuditEntryRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@DisplayName("AuditLogService")
class AuditLogServiceTest {

    private final AuditEntryRepository repo =
            mock(AuditEntryRepository.class);
    private final AuditLogService service =
            new AuditLogService(repo);

    private final UUID orgId = UuidV7.generate();

    @Test
    @DisplayName("should save audit entry when logging")
    void shouldSaveEntryWhenLogging() {
        when(repo.save(any(AuditEntry.class)))
                .thenAnswer(i -> i.getArgument(0));
        service.log(orgId, "dev", "Item",
                UuidV7.generate(), "Furnace",
                AuditAction.CREATE, null);
        verify(repo).save(any(AuditEntry.class));
    }

    @Test
    @DisplayName("should populate all fields on entry")
    void shouldPopulateAllFieldsWhenLogging() {
        UUID entityId = UuidV7.generate();
        when(repo.save(any(AuditEntry.class)))
                .thenAnswer(i -> {
                    AuditEntry e = i.getArgument(0);
                    assertThat(e.getOrganizationId())
                            .isEqualTo(orgId);
                    assertThat(e.getUsername())
                            .isEqualTo("dev");
                    assertThat(e.getEntityType())
                            .isEqualTo("Item");
                    assertThat(e.getEntityId())
                            .isEqualTo(entityId);
                    assertThat(e.getEntityName())
                            .isEqualTo("Furnace");
                    assertThat(e.getAction())
                            .isEqualTo(AuditAction.CREATE);
                    assertThat(e.getDetails()).isNull();
                    return e;
                });
        service.log(orgId, "dev", "Item",
                entityId, "Furnace",
                AuditAction.CREATE, null);
    }

    @Test
    @DisplayName("should include details when provided")
    void shouldIncludeDetailsWhenProvided() {
        when(repo.save(any(AuditEntry.class)))
                .thenAnswer(i -> {
                    AuditEntry e = i.getArgument(0);
                    assertThat(e.getDetails())
                            .isEqualTo("updated name");
                    return e;
                });
        service.log(orgId, "dev", "Item",
                UuidV7.generate(), "Furnace",
                AuditAction.UPDATE, "updated name");
    }

    @Test
    @DisplayName("should delegate to repository for"
            + " recent entries")
    void shouldDelegateRecentQuery() {
        UUID eid = UuidV7.generate();
        AuditEntry entry = new AuditEntry();
        entry.setEntityId(eid);
        when(repo.findRecentByOrganizationId(orgId, 10))
                .thenReturn(List.of(entry));
        List<AuditEntry> result =
                service.findRecentByOrganization(
                        orgId, 10);
        assertThat(result).hasSize(1);
        verify(repo).findRecentByOrganizationId(
                orgId, 10);
    }

    @Test
    @DisplayName("should delegate to repository for"
            + " entity entries")
    void shouldDelegateEntityQuery() {
        UUID entityId = UuidV7.generate();
        AuditEntry entry = new AuditEntry();
        entry.setEntityId(entityId);
        when(repo.findByEntityId(entityId))
                .thenReturn(List.of(entry));
        List<AuditEntry> result =
                service.findByEntityId(entityId);
        assertThat(result).hasSize(1);
        verify(repo).findByEntityId(entityId);
    }
}
