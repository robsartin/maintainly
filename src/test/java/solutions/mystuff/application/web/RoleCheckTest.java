package solutions.mystuff.application.web;

import java.security.Principal;
import java.util.List;
import java.util.UUID;

import solutions.mystuff.domain.model.AppRole;
import solutions.mystuff.domain.model.AppUser;
import solutions.mystuff.domain.model.UserGroup;
import solutions.mystuff.domain.port.in.GroupQuery;
import solutions.mystuff.domain.port.in.UserResolver;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@DisplayName("RoleCheck")
class RoleCheckTest {

    private final UserResolver userResolver =
            mock(UserResolver.class);
    private final GroupQuery groupQuery =
            mock(GroupQuery.class);
    private final RoleCheck roleCheck =
            new RoleCheck(userResolver, groupQuery);

    @Test
    @DisplayName("should use direct role when set")
    void shouldUseDirectRoleWhenSet() {
        AppUser user = new AppUser(
                UUID.randomUUID(), "alice");
        user.setRole(AppRole.VIEWER);
        when(userResolver.resolveOrCreate("alice"))
                .thenReturn(user);

        Principal p = () -> "alice";
        assertThat(roleCheck.canWrite(p)).isFalse();
        assertThat(roleCheck.isAdmin(p)).isFalse();
    }

    @Test
    @DisplayName("should allow admin to write and delete")
    void shouldAllowAdminAllAccess() {
        AppUser user = new AppUser(
                UUID.randomUUID(), "admin");
        user.setRole(AppRole.ADMIN);
        when(userResolver.resolveOrCreate("admin"))
                .thenReturn(user);

        Principal p = () -> "admin";
        assertThat(roleCheck.canWrite(p)).isTrue();
        assertThat(roleCheck.canDelete(p)).isTrue();
        assertThat(roleCheck.isAdmin(p)).isTrue();
    }

    @Test
    @DisplayName("should allow technician to write but not delete")
    void shouldAllowTechnicianWriteNotDelete() {
        AppUser user = new AppUser(
                UUID.randomUUID(), "tech");
        user.setRole(AppRole.TECHNICIAN);
        when(userResolver.resolveOrCreate("tech"))
                .thenReturn(user);

        Principal p = () -> "tech";
        assertThat(roleCheck.canWrite(p)).isTrue();
        assertThat(roleCheck.canDelete(p)).isFalse();
    }

    @Test
    @DisplayName("should use group role when no direct role")
    void shouldUseGroupRoleWhenNoDirect() {
        UUID userId = UUID.randomUUID();
        AppUser user = new AppUser(userId, "bob");
        when(userResolver.resolveOrCreate("bob"))
                .thenReturn(user);
        UserGroup group = new UserGroup(
                UUID.randomUUID(), "Viewers",
                AppRole.VIEWER);
        when(groupQuery.findGroupsForUser(userId))
                .thenReturn(List.of(group));

        Principal p = () -> "bob";
        assertThat(roleCheck.canWrite(p)).isFalse();
    }

    @Test
    @DisplayName("should use highest group role")
    void shouldUseHighestGroupRole() {
        UUID userId = UUID.randomUUID();
        AppUser user = new AppUser(userId, "carol");
        when(userResolver.resolveOrCreate("carol"))
                .thenReturn(user);
        UserGroup viewerGroup = new UserGroup(
                UUID.randomUUID(), "Viewers",
                AppRole.VIEWER);
        UserGroup adminGroup = new UserGroup(
                UUID.randomUUID(), "Admins",
                AppRole.ADMIN);
        when(groupQuery.findGroupsForUser(userId))
                .thenReturn(List.of(
                        viewerGroup, adminGroup));

        Principal p = () -> "carol";
        assertThat(roleCheck.isAdmin(p)).isTrue();
    }

    @Test
    @DisplayName("should default to admin when no role set")
    void shouldDefaultToAdminWhenNoRole() {
        UUID userId = UUID.randomUUID();
        AppUser user = new AppUser(userId, "newuser");
        when(userResolver.resolveOrCreate("newuser"))
                .thenReturn(user);
        when(groupQuery.findGroupsForUser(userId))
                .thenReturn(List.of());

        Principal p = () -> "newuser";
        assertThat(roleCheck.isAdmin(p)).isTrue();
    }

    @Test
    @DisplayName("should allow facility manager to delete")
    void shouldAllowFacilityManagerToDelete() {
        AppUser user = new AppUser(
                UUID.randomUUID(), "fm");
        user.setRole(AppRole.FACILITY_MANAGER);
        when(userResolver.resolveOrCreate("fm"))
                .thenReturn(user);

        Principal p = () -> "fm";
        assertThat(roleCheck.canDelete(p)).isTrue();
        assertThat(roleCheck.canWrite(p)).isTrue();
        assertThat(roleCheck.isAdmin(p)).isFalse();
    }
}
