package com.robsartin.maintainly.domain.port.out;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import com.robsartin.maintainly.domain.model.Item;

public interface ItemRepository {

    List<Item> findByOrganizationId(UUID organizationId);

    List<Item> searchByOrganizationId(
            UUID organizationId, String query);

    Optional<Item> findByIdAndOrganizationId(
            UUID id, UUID organizationId);

    Item save(Item item);
}
