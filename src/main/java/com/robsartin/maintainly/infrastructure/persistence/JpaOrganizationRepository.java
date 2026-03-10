package com.robsartin.maintainly.infrastructure.persistence;

import java.util.UUID;

import com.robsartin.maintainly.domain.model.Organization;
import com.robsartin.maintainly.domain.port.out.OrganizationRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface JpaOrganizationRepository
        extends JpaRepository<Organization, UUID>,
        OrganizationRepository {
}
