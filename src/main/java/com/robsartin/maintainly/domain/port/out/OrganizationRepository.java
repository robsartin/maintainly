package com.robsartin.maintainly.domain.port.out;

import java.util.Optional;
import java.util.UUID;

import com.robsartin.maintainly.domain.model.Organization;

public interface OrganizationRepository {

    Optional<Organization> findById(UUID id);

    Organization save(Organization organization);
}
