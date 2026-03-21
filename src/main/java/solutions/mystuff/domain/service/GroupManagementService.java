package solutions.mystuff.domain.service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import solutions.mystuff.domain.model.AppRole;
import solutions.mystuff.domain.model.AppUser;
import solutions.mystuff.domain.model.NotFoundException;
import solutions.mystuff.domain.model.UserGroup;
import solutions.mystuff.domain.model.UserGroupMembership;
import solutions.mystuff.domain.model.Validation;
import solutions.mystuff.domain.port.in.GroupManagement;
import solutions.mystuff.domain.port.in.GroupQuery;
import solutions.mystuff.domain.port.out.AppUserRepository;
import solutions.mystuff.domain.port.out.UserGroupMembershipRepository;
import solutions.mystuff.domain.port.out.UserGroupRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation
        .Transactional;

/**
 * Creates, updates, deletes, and queries user groups.
 *
 * <div class="mermaid">
 * sequenceDiagram
 *     Controller->>GroupManagementService: createGroup(...)
 *     GroupManagementService->>UserGroupRepository: save(group)
 *     UserGroupRepository-->>GroupManagementService: saved group
 *     GroupManagementService-->>Controller: UserGroup
 * </div>
 *
 * @see GroupManagement
 * @see GroupQuery
 */
@Service
@Transactional
public class GroupManagementService
        implements GroupManagement, GroupQuery {

    private static final int MAX_NAME = 100;
    private static final int MAX_DESC = 500;

    private final UserGroupRepository groupRepo;
    private final UserGroupMembershipRepository memberRepo;
    private final AppUserRepository userRepo;

    public GroupManagementService(
            UserGroupRepository groupRepo,
            UserGroupMembershipRepository memberRepo,
            AppUserRepository userRepo) {
        this.groupRepo = groupRepo;
        this.memberRepo = memberRepo;
        this.userRepo = userRepo;
    }

    @Override
    public UserGroup createGroup(UUID orgId, String name,
            AppRole role, String description) {
        Validation.requireNotBlank(name, "Group name");
        Validation.requireMaxLength(
                name, "Group name", MAX_NAME);
        Validation.requireMaxLength(
                description, "Description", MAX_DESC);
        if (role == null) {
            throw new IllegalArgumentException(
                    "Role is required");
        }
        UserGroup group = new UserGroup(
                orgId, name.trim(), role);
        group.setDescription(
                Validation.trimOrNull(description));
        return groupRepo.save(group);
    }

    @Override
    public UserGroup updateGroup(UUID orgId,
            UUID groupId, String name, AppRole role,
            String description) {
        Validation.requireNotBlank(name, "Group name");
        Validation.requireMaxLength(
                name, "Group name", MAX_NAME);
        Validation.requireMaxLength(
                description, "Description", MAX_DESC);
        if (role == null) {
            throw new IllegalArgumentException(
                    "Role is required");
        }
        UserGroup group = requireGroup(orgId, groupId);
        group.setName(name.trim());
        group.setRole(role);
        group.setDescription(
                Validation.trimOrNull(description));
        return groupRepo.save(group);
    }

    @Override
    public void deleteGroup(UUID orgId, UUID groupId) {
        UserGroup group = requireGroup(orgId, groupId);
        memberRepo.deleteByGroup(group);
        groupRepo.deleteById(groupId);
    }

    @Override
    public void addMember(UUID orgId, UUID groupId,
            UUID userId) {
        UserGroup group = requireGroup(orgId, groupId);
        AppUser user = userRepo.findById(userId)
                .orElseThrow(() -> new NotFoundException(
                        "User not found"));
        Optional<UserGroupMembership> existing =
                memberRepo.findByGroupAndUserId(
                        group, userId);
        if (existing.isPresent()) {
            return;
        }
        memberRepo.save(
                new UserGroupMembership(group, userId));
    }

    @Override
    public void removeMember(UUID orgId, UUID groupId,
            UUID userId) {
        UserGroup group = requireGroup(orgId, groupId);
        UserGroupMembership membership =
                memberRepo.findByGroupAndUserId(
                        group, userId)
                .orElseThrow(() -> new NotFoundException(
                        "Membership not found"));
        memberRepo.delete(membership);
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserGroup> findAllGroups(UUID orgId) {
        return groupRepo.findByOrganizationId(orgId);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<UserGroup> findGroup(
            UUID groupId, UUID orgId) {
        return groupRepo.findByIdAndOrganizationId(
                groupId, orgId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserGroupMembership> findMembers(
            UUID groupId) {
        UserGroup group = new UserGroup();
        group.setId(groupId);
        return memberRepo.findByGroup(group);
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserGroup> findGroupsForUser(
            UUID userId) {
        List<UserGroupMembership> memberships =
                memberRepo.findByUserId(userId);
        return memberships.stream()
                .map(UserGroupMembership::getGroup)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<AppUser> findOrgUsers(UUID orgId) {
        return userRepo.findByOrganizationId(orgId);
    }

    private UserGroup requireGroup(
            UUID orgId, UUID groupId) {
        return groupRepo.findByIdAndOrganizationId(
                groupId, orgId)
                .orElseThrow(() -> new NotFoundException(
                        "Group not found"));
    }
}
