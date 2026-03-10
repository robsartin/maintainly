package com.robsartin.maintainly.infrastructure.persistence;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import com.robsartin.maintainly.domain.model.Item;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

interface SpringDataItemRepository
        extends JpaRepository<Item, UUID> {

    List<Item> findByOrganizationId(UUID organizationId);

    Page<Item> findByOrganizationId(
            UUID organizationId, Pageable pageable);

    @Query("SELECT i FROM Item i "
            + "WHERE i.organizationId = :orgId "
            + "AND (LOWER(i.name) LIKE "
            + "LOWER(CONCAT('%', :q, '%')) "
            + "OR LOWER(i.location) LIKE "
            + "LOWER(CONCAT('%', :q, '%')))")
    List<Item> searchByOrganizationId(
            @Param("orgId") UUID organizationId,
            @Param("q") String query);

    @Query(value = "SELECT i FROM Item i "
            + "WHERE i.organizationId = :orgId "
            + "AND (LOWER(i.name) LIKE "
            + "LOWER(CONCAT('%', :q, '%')) "
            + "OR LOWER(i.location) LIKE "
            + "LOWER(CONCAT('%', :q, '%')))",
            countQuery = "SELECT count(i) FROM Item i "
            + "WHERE i.organizationId = :orgId "
            + "AND (LOWER(i.name) LIKE "
            + "LOWER(CONCAT('%', :q, '%')) "
            + "OR LOWER(i.location) LIKE "
            + "LOWER(CONCAT('%', :q, '%')))")
    Page<Item> searchByOrganizationId(
            @Param("orgId") UUID organizationId,
            @Param("q") String query,
            Pageable pageable);

    Optional<Item> findByIdAndOrganizationId(
            UUID id, UUID organizationId);
}
