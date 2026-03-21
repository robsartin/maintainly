package solutions.mystuff.domain.model;

import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;

/**
 * Named group that assigns an {@link AppRole} to its members.
 *
 * <p>Each group belongs to an organization and carries a single
 * role. Users added to the group inherit that role.
 *
 * <div class="mermaid">
 * classDiagram
 *     class UserGroup {
 *         String name
 *         AppRole role
 *         UUID organizationId
 *         String description
 *     }
 *     class OrgOwnedEntity {
 *         UUID organizationId
 *     }
 *     class AppRole
 *     UserGroup --|> OrgOwnedEntity
 *     UserGroup --> AppRole
 * </div>
 *
 * @see AppRole
 * @see UserGroupMembership
 * @see OrgOwnedEntity
 */
@Entity
@Table(name = "user_groups")
public class UserGroup extends OrgOwnedEntity {

    @Column(nullable = false, length = 100)
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private AppRole role;

    @Column(length = 500)
    private String description;

    public UserGroup() {
    }

    public UserGroup(UUID orgId, String name,
            AppRole role) {
        setOrganizationId(orgId);
        this.name = name;
        this.role = role;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public AppRole getRole() {
        return role;
    }

    public void setRole(AppRole role) {
        this.role = role;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
