package com.robsartin.maintainly.infrastructure.persistence;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import com.robsartin.maintainly.domain.model.Item;
import com.robsartin.maintainly.domain.port.out.ItemRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface JpaItemRepository
        extends JpaRepository<Item, UUID>,
        ItemRepository {

    @Override
    List<Item> findByOrganizationId(UUID organizationId);

    @Override
    @Query("SELECT i FROM Item i "
            + "WHERE i.organizationId = :orgId "
            + "AND (LOWER(i.name) LIKE "
            + "LOWER(CONCAT('%', :q, '%')) "
            + "OR LOWER(i.location) LIKE "
            + "LOWER(CONCAT('%', :q, '%')))")
    List<Item> searchByOrganizationId(
            @Param("orgId") UUID organizationId,
            @Param("q") String query);

    @Override
    Optional<Item> findByIdAndOrganizationId(
            UUID id, UUID organizationId);
}
