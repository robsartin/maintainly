package com.robsartin.maintainly.domain.port.out;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import com.robsartin.maintainly.domain.model.Property;

public interface PropertyRepository {

    List<Property> findByOrganizationIdOrderByNextServiceDate(
            int organizationId);

    List<Property> searchByOrganizationId(
            int organizationId, String query);

    Optional<Property> findById(UUID id);

    Property save(Property property);
}
