package solutions.mystuff.domain.model;

import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

/**
 * Links an {@link AppUser} to a {@link UserGroup}.
 *
 * <p>Each membership record pairs one user with one group.
 * The combination of user and group is unique.
 *
 * <div class="mermaid">
 * classDiagram
 *     class UserGroupMembership {
 *         UUID userId
 *     }
 *     class UserGroup
 *     class AppUser
 *     UserGroupMembership --> UserGroup
 *     UserGroupMembership --> AppUser : userId
 *     UserGroupMembership --|> BaseEntity
 * </div>
 *
 * @see UserGroup
 * @see AppUser
 */
@Entity
@Table(name = "user_group_memberships",
        uniqueConstraints = @UniqueConstraint(
                columnNames = {"group_id", "user_id"}))
public class UserGroupMembership extends BaseEntity {

    @ManyToOne
    @JoinColumn(name = "group_id", nullable = false)
    private UserGroup group;

    @Column(name = "user_id", nullable = false)
    private UUID userId;

    public UserGroupMembership() {
    }

    public UserGroupMembership(UserGroup group,
            UUID userId) {
        this.group = group;
        this.userId = userId;
    }

    public UserGroup getGroup() {
        return group;
    }

    public void setGroup(UserGroup group) {
        this.group = group;
    }

    public UUID getUserId() {
        return userId;
    }

    public void setUserId(UUID userId) {
        this.userId = userId;
    }
}
