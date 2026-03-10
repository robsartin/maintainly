package com.robsartin.maintainly.infrastructure.persistence;

import java.util.List;
import java.util.UUID;

import com.robsartin.maintainly.domain.model.Property;
import com.robsartin.maintainly.domain.port.out.PropertyRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface JpaPropertyRepository
        extends JpaRepository<Property, UUID>,
        PropertyRepository {

    @Override
    @Query("SELECT p FROM Property p "
            + "WHERE p.organizationId = :orgId "
            + "ORDER BY p.nextServiceDate ASC NULLS LAST")
    List<Property> findByOrganizationIdOrderByNextServiceDate(
            @Param("orgId") UUID organizationId);

    @Override
    @Query("SELECT p FROM Property p "
            + "WHERE p.organizationId = :orgId "
            + "AND (LOWER(p.name) LIKE "
            + "LOWER(CONCAT('%', :q, '%')) "
            + "OR LOWER(p.address) LIKE "
            + "LOWER(CONCAT('%', :q, '%')))"
            + "ORDER BY p.nextServiceDate ASC NULLS LAST")
    List<Property> searchByOrganizationId(
            @Param("orgId") UUID organizationId,
            @Param("q") String query);
}
