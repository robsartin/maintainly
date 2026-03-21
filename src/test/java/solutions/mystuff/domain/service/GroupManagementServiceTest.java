package solutions.mystuff.domain.service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import solutions.mystuff.domain.model.AppRole;
import solutions.mystuff.domain.model.AppUser;
import solutions.mystuff.domain.model.NotFoundException;
import solutions.mystuff.domain.model.UserGroup;
import solutions.mystuff.domain.model.UserGroupMembership;
import solutions.mystuff.domain.port.out.AppUserRepository;
import solutions.mystuff.domain.port.out
        .UserGroupMembershipRepository;
import solutions.mystuff.domain.port.out
        .UserGroupRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions
        .assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@DisplayName("GroupManagementService")
class GroupManagementServiceTest {

    private final UserGroupRepository groupRepo =
            mock(UserGroupRepository.class);
    private final UserGroupMembershipRepository memberRepo =
            mock(UserGroupMembershipRepository.class);
    private final AppUserRepository userRepo =
            mock(AppUserRepository.class);
    private final GroupManagementService service =
            new GroupManagementService(
                    groupRepo, memberRepo, userRepo);

    private final UUID orgId = UUID.randomUUID();

    @Test
    @DisplayName("should create group with valid data")
    void shouldCreateGroupWithValidData() {
        when(groupRepo.save(any(UserGroup.class)))
                .thenAnswer(i -> i.getArgument(0));

        UserGroup result = service.createGroup(
                orgId, "Admins", AppRole.ADMIN,
                "Admin group");

        assertThat(result.getName())
                .isEqualTo("Admins");
        assertThat(result.getRole())
                .isEqualTo(AppRole.ADMIN);
        assertThat(result.getDescription())
                .isEqualTo("Admin group");
        verify(groupRepo).save(any(UserGroup.class));
    }

    @Test
    @DisplayName("should reject blank group name")
    void shouldRejectBlankGroupName() {
        assertThatThrownBy(() ->
                service.createGroup(orgId, "  ",
                        AppRole.ADMIN, null))
                .isInstanceOf(
                        IllegalArgumentException.class)
                .hasMessageContaining("required");
    }

    @Test
    @DisplayName("should reject null role")
    void shouldRejectNullRole() {
        assertThatThrownBy(() ->
                service.createGroup(orgId, "Test",
                        null, null))
                .isInstanceOf(
                        IllegalArgumentException.class)
                .hasMessageContaining("Role");
    }

    @Test
    @DisplayName("should update existing group")
    void shouldUpdateExistingGroup() {
        UUID groupId = UUID.randomUUID();
        UserGroup existing = new UserGroup(
                orgId, "Old", AppRole.VIEWER);
        existing.setId(groupId);
        when(groupRepo.findByIdAndOrganizationId(
                groupId, orgId))
                .thenReturn(Optional.of(existing));
        when(groupRepo.save(any(UserGroup.class)))
                .thenAnswer(i -> i.getArgument(0));

        UserGroup result = service.updateGroup(
                orgId, groupId, "New",
                AppRole.ADMIN, "Updated");

        assertThat(result.getName()).isEqualTo("New");
        assertThat(result.getRole())
                .isEqualTo(AppRole.ADMIN);
    }

    @Test
    @DisplayName("should throw when updating nonexistent group")
    void shouldThrowWhenUpdatingNonexistent() {
        UUID groupId = UUID.randomUUID();
        when(groupRepo.findByIdAndOrganizationId(
                groupId, orgId))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() ->
                service.updateGroup(orgId, groupId,
                        "Name", AppRole.ADMIN, null))
                .isInstanceOf(NotFoundException.class)
                .hasMessage("Group not found");
    }

    @Test
    @DisplayName("should delete group and its memberships")
    void shouldDeleteGroupAndMemberships() {
        UUID groupId = UUID.randomUUID();
        UserGroup group = new UserGroup(
                orgId, "ToDelete", AppRole.VIEWER);
        group.setId(groupId);
        when(groupRepo.findByIdAndOrganizationId(
                groupId, orgId))
                .thenReturn(Optional.of(group));

        service.deleteGroup(orgId, groupId);

        verify(memberRepo).deleteByGroup(group);
        verify(groupRepo).deleteById(groupId);
    }

    @Test
    @DisplayName("should add member to group")
    void shouldAddMemberToGroup() {
        UUID groupId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        UserGroup group = new UserGroup(
                orgId, "Group", AppRole.ADMIN);
        group.setId(groupId);
        when(groupRepo.findByIdAndOrganizationId(
                groupId, orgId))
                .thenReturn(Optional.of(group));
        when(userRepo.findById(userId))
                .thenReturn(Optional.of(new AppUser()));
        when(memberRepo.findByGroupAndUserId(
                group, userId))
                .thenReturn(Optional.empty());

        service.addMember(orgId, groupId, userId);

        verify(memberRepo).save(
                any(UserGroupMembership.class));
    }

    @Test
    @DisplayName("should not duplicate membership")
    void shouldNotDuplicateMembership() {
        UUID groupId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        UserGroup group = new UserGroup(
                orgId, "Group", AppRole.ADMIN);
        group.setId(groupId);
        when(groupRepo.findByIdAndOrganizationId(
                groupId, orgId))
                .thenReturn(Optional.of(group));
        when(userRepo.findById(userId))
                .thenReturn(Optional.of(new AppUser()));
        when(memberRepo.findByGroupAndUserId(
                group, userId))
                .thenReturn(Optional.of(
                        new UserGroupMembership()));

        service.addMember(orgId, groupId, userId);

        verify(memberRepo).findByGroupAndUserId(
                group, userId);
    }

    @Test
    @DisplayName("should remove member from group")
    void shouldRemoveMember() {
        UUID groupId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        UserGroup group = new UserGroup(
                orgId, "Group", AppRole.ADMIN);
        group.setId(groupId);
        UserGroupMembership membership =
                new UserGroupMembership(group, userId);
        when(groupRepo.findByIdAndOrganizationId(
                groupId, orgId))
                .thenReturn(Optional.of(group));
        when(memberRepo.findByGroupAndUserId(
                group, userId))
                .thenReturn(Optional.of(membership));

        service.removeMember(orgId, groupId, userId);

        verify(memberRepo).delete(membership);
    }

    @Test
    @DisplayName("should throw when removing nonexistent member")
    void shouldThrowWhenRemovingNonexistent() {
        UUID groupId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        UserGroup group = new UserGroup(
                orgId, "Group", AppRole.ADMIN);
        group.setId(groupId);
        when(groupRepo.findByIdAndOrganizationId(
                groupId, orgId))
                .thenReturn(Optional.of(group));
        when(memberRepo.findByGroupAndUserId(
                group, userId))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() ->
                service.removeMember(
                        orgId, groupId, userId))
                .isInstanceOf(NotFoundException.class)
                .hasMessage("Membership not found");
    }

    @Test
    @DisplayName("should find all groups for org")
    void shouldFindAllGroupsForOrg() {
        List<UserGroup> groups = List.of(
                new UserGroup(orgId, "A", AppRole.ADMIN),
                new UserGroup(orgId, "B", AppRole.VIEWER));
        when(groupRepo.findByOrganizationId(orgId))
                .thenReturn(groups);

        List<UserGroup> result =
                service.findAllGroups(orgId);

        assertThat(result).hasSize(2);
    }

    @Test
    @DisplayName("should find org users")
    void shouldFindOrgUsers() {
        List<AppUser> users = List.of(
                new AppUser(UUID.randomUUID(), "alice"),
                new AppUser(UUID.randomUUID(), "bob"));
        when(userRepo.findByOrganizationId(orgId))
                .thenReturn(users);

        List<AppUser> result =
                service.findOrgUsers(orgId);

        assertThat(result).hasSize(2);
    }

    @Test
    @DisplayName("should reject long group name")
    void shouldRejectLongGroupName() {
        String longName = "x".repeat(101);
        assertThatThrownBy(() ->
                service.createGroup(orgId, longName,
                        AppRole.ADMIN, null))
                .isInstanceOf(
                        IllegalArgumentException.class)
                .hasMessageContaining("maximum length");
    }
}
