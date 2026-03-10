package com.robsartin.maintainly.domain.port.out;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import com.robsartin.maintainly.domain.model.ServiceType;

public interface ServiceTypeRepository {

    List<ServiceType> findByOrganizationId(
            UUID organizationId);

    Optional<ServiceType> findByIdAndOrganizationId(
            UUID id, UUID organizationId);

    ServiceType save(ServiceType serviceType);
}
