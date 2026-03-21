package solutions.mystuff.domain.model;

import java.util.UUID;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertNull;

@DisplayName("UserGroup")
class UserGroupTest {

    @Test
    @DisplayName("should have default null values")
    void shouldHaveDefaults() {
        UserGroup group = new UserGroup();
        assertNull(group.getId());
        assertNull(group.getName());
        assertNull(group.getRole());
        assertNull(group.getDescription());
    }

    @Test
    @DisplayName("should construct with org, name, and role")
    void shouldConstruct() {
        UUID orgId = UUID.randomUUID();
        UserGroup group = new UserGroup(
                orgId, "Admins", AppRole.ADMIN);
        assertThat(group.getOrganizationId())
                .isEqualTo(orgId);
        assertThat(group.getName())
                .isEqualTo("Admins");
        assertThat(group.getRole())
                .isEqualTo(AppRole.ADMIN);
    }

    @Test
    @DisplayName("should set and get description")
    void shouldSetDescription() {
        UserGroup group = new UserGroup();
        group.setDescription("Test description");
        assertThat(group.getDescription())
                .isEqualTo("Test description");
    }

    @Test
    @DisplayName("should set and get name")
    void shouldSetName() {
        UserGroup group = new UserGroup();
        group.setName("Technicians");
        assertThat(group.getName())
                .isEqualTo("Technicians");
    }

    @Test
    @DisplayName("should set and get role")
    void shouldSetRole() {
        UserGroup group = new UserGroup();
        group.setRole(AppRole.VIEWER);
        assertThat(group.getRole())
                .isEqualTo(AppRole.VIEWER);
    }

    @Test
    @DisplayName("should belong to organization")
    void shouldBelongToOrganization() {
        UUID orgId = UUID.randomUUID();
        UserGroup group = new UserGroup(
                orgId, "Test", AppRole.ADMIN);
        assertThat(group.belongsTo(orgId)).isTrue();
        assertThat(group.belongsTo(UUID.randomUUID()))
                .isFalse();
    }
}
