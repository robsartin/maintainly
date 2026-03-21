package solutions.mystuff.infrastructure.persistence;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import solutions.mystuff.domain.model.UserGroup;
import solutions.mystuff.domain.model.UserGroupMembership;
import solutions.mystuff.domain.port.out
        .UserGroupMembershipRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA adapter for the
 * {@link UserGroupMembershipRepository} port.
 *
 * <div class="mermaid">
 * classDiagram
 *     class JpaUserGroupMembershipRepository
 *     class JpaRepository~UserGroupMembership, UUID~
 *     class UserGroupMembershipRepository
 *     JpaUserGroupMembershipRepository --|> JpaRepository~UserGroupMembership, UUID~
 *     JpaUserGroupMembershipRepository --|> UserGroupMembershipRepository
 * </div>
 *
 * @see UserGroupMembershipRepository
 * @see UserGroupMembership
 */
@Repository
public interface JpaUserGroupMembershipRepository
        extends JpaRepository<UserGroupMembership, UUID>,
        UserGroupMembershipRepository {

    @Override
    List<UserGroupMembership> findByGroup(
            UserGroup group);

    @Override
    List<UserGroupMembership> findByUserId(UUID userId);

    @Override
    Optional<UserGroupMembership> findByGroupAndUserId(
            UserGroup group, UUID userId);

    @Override
    void deleteByGroup(UserGroup group);
}
