package solutions.mystuff.infrastructure.persistence;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import solutions.mystuff.domain.model.UserGroup;
import solutions.mystuff.domain.port.out.UserGroupRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA adapter for the {@link UserGroupRepository} port.
 *
 * <div class="mermaid">
 * classDiagram
 *     class JpaUserGroupRepository
 *     class JpaRepository~UserGroup, UUID~
 *     class UserGroupRepository
 *     JpaUserGroupRepository --|> JpaRepository~UserGroup, UUID~
 *     JpaUserGroupRepository --|> UserGroupRepository
 * </div>
 *
 * @see UserGroupRepository
 * @see UserGroup
 */
@Repository
public interface JpaUserGroupRepository
        extends JpaRepository<UserGroup, UUID>,
        UserGroupRepository {

    @Override
    List<UserGroup> findByOrganizationId(
            UUID organizationId);

    @Override
    Optional<UserGroup> findByIdAndOrganizationId(
            UUID id, UUID organizationId);
}
