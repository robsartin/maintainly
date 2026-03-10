package com.robsartin.maintainly.domain.port.out;

import java.util.Optional;

import com.robsartin.maintainly.domain.model.Organization;

public interface OrganizationRepository {

    Optional<Organization> findById(int id);

    Organization save(Organization organization);
}
