package solutions.mystuff.application.web;

import java.security.Principal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import solutions.mystuff.domain.model.AppRole;
import solutions.mystuff.domain.model.AppUser;
import solutions.mystuff.domain.model.UserGroup;
import solutions.mystuff.domain.model.UserGroupMembership;
import solutions.mystuff.domain.port.in.GroupManagement;
import solutions.mystuff.domain.port.in.GroupQuery;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.security.access.prepost
        .PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc
        .support.RedirectAttributes;

/**
 * Handles user group CRUD and membership at /settings/groups.
 *
 * <div class="mermaid">
 * sequenceDiagram
 *     Browser->>GroupController: GET/POST /settings/groups
 *     GroupController->>ControllerHelper: resolveUser
 *     GroupController->>GroupManagement: createGroup/updateGroup
 *     GroupController->>GroupQuery: findAllGroups
 *     GroupController-->>Browser: Thymeleaf view
 * </div>
 *
 * @see ControllerHelper
 * @see GroupManagement
 * @see GroupQuery
 */
@Controller
@Tag(name = "Groups",
        description = "User group management")
public class GroupController {

    private final ControllerHelper helper;
    private final GroupManagement groupService;
    private final GroupQuery groupQuery;

    public GroupController(
            ControllerHelper helper,
            GroupManagement groupService,
            GroupQuery groupQuery) {
        this.helper = helper;
        this.groupService = groupService;
        this.groupQuery = groupQuery;
    }

    @Operation(summary = "List groups",
            description = "Returns all groups for the"
                    + " organization with members.",
            responses = @ApiResponse(
                    responseCode = "200",
                    description = "HTML groups page"))
    @GetMapping("/settings/groups")
    @PreAuthorize("@roleCheck.isAdmin(#principal)")
    public String listGroups(
            Principal principal, Model model) {
        AppUser user = helper.resolveUser(principal);
        helper.addUserAttrs(user, model);
        UUID orgId = user.getOrganization().getId();
        List<UserGroup> groups =
                groupQuery.findAllGroups(orgId);
        Map<UUID, List<UserGroupMembership>> memberMap =
                buildMemberMap(groups);
        model.addAttribute("groups", groups);
        model.addAttribute("memberMap", memberMap);
        model.addAttribute("roles", AppRole.values());
        model.addAttribute("orgUsers",
                groupQuery.findOrgUsers(orgId));
        return "groups";
    }

    @Operation(summary = "Create group",
            responses = @ApiResponse(
                    responseCode = "302",
                    description = "Redirect to groups"))
    @PostMapping("/settings/groups")
    @PreAuthorize("@roleCheck.isAdmin(#principal)")
    public String createGroup(
            @Parameter(description = "Group name")
            @RequestParam String name,
            @Parameter(description = "Role")
            @RequestParam String role,
            @Parameter(description = "Description")
            @RequestParam(required = false)
                    String description,
            Principal principal,
            RedirectAttributes redirectAttrs) {
        AppUser user = helper.resolveUser(principal);
        helper.setOrgMdc(user);
        UUID orgId = user.getOrganization().getId();
        groupService.createGroup(orgId, name,
                AppRole.valueOf(role), description);
        redirectAttrs.addFlashAttribute(
                "success", "Group created");
        return "redirect:/settings/groups";
    }

    @Operation(summary = "Update group",
            responses = @ApiResponse(
                    responseCode = "302",
                    description = "Redirect to groups"))
    @PutMapping("/settings/groups/{id}")
    @PreAuthorize("@roleCheck.isAdmin(#principal)")
    public String updateGroup(
            @Parameter(description = "Group UUID")
            @PathVariable("id") UUID groupId,
            @Parameter(description = "Group name")
            @RequestParam String name,
            @Parameter(description = "Role")
            @RequestParam String role,
            @Parameter(description = "Description")
            @RequestParam(required = false)
                    String description,
            Principal principal,
            RedirectAttributes redirectAttrs) {
        AppUser user = helper.resolveUser(principal);
        helper.setOrgMdc(user);
        UUID orgId = user.getOrganization().getId();
        groupService.updateGroup(orgId, groupId, name,
                AppRole.valueOf(role), description);
        redirectAttrs.addFlashAttribute(
                "success", "Group updated");
        return "redirect:/settings/groups";
    }

    @Operation(summary = "Delete group",
            responses = @ApiResponse(
                    responseCode = "302",
                    description = "Redirect to groups"))
    @DeleteMapping("/settings/groups/{id}")
    @PreAuthorize("@roleCheck.isAdmin(#principal)")
    public String deleteGroup(
            @Parameter(description = "Group UUID")
            @PathVariable("id") UUID groupId,
            Principal principal,
            RedirectAttributes redirectAttrs) {
        AppUser user = helper.resolveUser(principal);
        helper.setOrgMdc(user);
        UUID orgId = user.getOrganization().getId();
        groupService.deleteGroup(orgId, groupId);
        redirectAttrs.addFlashAttribute(
                "success", "Group deleted");
        return "redirect:/settings/groups";
    }

    @Operation(summary = "Add member to group",
            responses = @ApiResponse(
                    responseCode = "302",
                    description = "Redirect to groups"))
    @PostMapping("/settings/groups/{id}/members")
    @PreAuthorize("@roleCheck.isAdmin(#principal)")
    public String addMember(
            @Parameter(description = "Group UUID")
            @PathVariable("id") UUID groupId,
            @Parameter(description = "User UUID")
            @RequestParam UUID userId,
            Principal principal,
            RedirectAttributes redirectAttrs) {
        AppUser user = helper.resolveUser(principal);
        helper.setOrgMdc(user);
        UUID orgId = user.getOrganization().getId();
        groupService.addMember(orgId, groupId, userId);
        redirectAttrs.addFlashAttribute(
                "success", "Member added");
        return "redirect:/settings/groups";
    }

    @Operation(summary = "Remove member from group",
            responses = @ApiResponse(
                    responseCode = "302",
                    description = "Redirect to groups"))
    @DeleteMapping(
            "/settings/groups/{id}/members/{userId}")
    @PreAuthorize("@roleCheck.isAdmin(#principal)")
    public String removeMember(
            @Parameter(description = "Group UUID")
            @PathVariable("id") UUID groupId,
            @Parameter(description = "User UUID")
            @PathVariable UUID userId,
            Principal principal,
            RedirectAttributes redirectAttrs) {
        AppUser user = helper.resolveUser(principal);
        helper.setOrgMdc(user);
        UUID orgId = user.getOrganization().getId();
        groupService.removeMember(
                orgId, groupId, userId);
        redirectAttrs.addFlashAttribute(
                "success", "Member removed");
        return "redirect:/settings/groups";
    }

    private Map<UUID, List<UserGroupMembership>>
            buildMemberMap(List<UserGroup> groups) {
        Map<UUID, List<UserGroupMembership>> map =
                new HashMap<>();
        for (UserGroup g : groups) {
            map.put(g.getId(),
                    groupQuery.findMembers(g.getId()));
        }
        return map;
    }
}
