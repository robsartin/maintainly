package solutions.mystuff.domain.model;

import java.util.UUID;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertNull;

@DisplayName("UserGroupMembership")
class UserGroupMembershipTest {

    @Test
    @DisplayName("should have default null values")
    void shouldHaveDefaults() {
        UserGroupMembership m =
                new UserGroupMembership();
        assertNull(m.getId());
        assertNull(m.getGroup());
        assertNull(m.getUserId());
    }

    @Test
    @DisplayName("should construct with group and userId")
    void shouldConstruct() {
        UUID orgId = UUID.randomUUID();
        UserGroup group = new UserGroup(
                orgId, "Test", AppRole.ADMIN);
        UUID userId = UUID.randomUUID();
        UserGroupMembership m =
                new UserGroupMembership(group, userId);
        assertThat(m.getGroup()).isEqualTo(group);
        assertThat(m.getUserId()).isEqualTo(userId);
    }

    @Test
    @DisplayName("should set and get group")
    void shouldSetGroup() {
        UserGroupMembership m =
                new UserGroupMembership();
        UserGroup group = new UserGroup();
        m.setGroup(group);
        assertThat(m.getGroup()).isEqualTo(group);
    }

    @Test
    @DisplayName("should set and get userId")
    void shouldSetUserId() {
        UserGroupMembership m =
                new UserGroupMembership();
        UUID userId = UUID.randomUUID();
        m.setUserId(userId);
        assertThat(m.getUserId()).isEqualTo(userId);
    }
}
