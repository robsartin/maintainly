package com.robsartin.maintainly.infrastructure.persistence;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import com.robsartin.maintainly.domain.model.ServiceType;
import com.robsartin.maintainly.domain.port.out.ServiceTypeRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface JpaServiceTypeRepository
        extends JpaRepository<ServiceType, UUID>,
        ServiceTypeRepository {

    @Override
    List<ServiceType> findByOrganizationId(
            UUID organizationId);

    @Override
    Optional<ServiceType> findByIdAndOrganizationId(
            UUID id, UUID organizationId);
}
