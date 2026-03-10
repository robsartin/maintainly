package com.robsartin.maintainly.domain.port.out;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import com.robsartin.maintainly.domain.model.Item;
import com.robsartin.maintainly.domain.model.PageResult;

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
