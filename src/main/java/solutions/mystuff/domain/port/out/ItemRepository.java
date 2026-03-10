package solutions.mystuff.domain.port.out;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import solutions.mystuff.domain.model.Item;
import solutions.mystuff.domain.model.PageResult;

public interface ItemRepository {

    List<Item> findByOrganizationId(UUID organizationId);

    PageResult<Item> findByOrganizationId(
            UUID organizationId, int page, int size);

    List<Item> searchByOrganizationId(
            UUID organizationId, String query);

    PageResult<Item> searchByOrganizationId(
            UUID organizationId, String query,
            int page, int size);

    Optional<Item> findByIdAndOrganizationId(
            UUID id, UUID organizationId);

    Item save(Item item);
}
