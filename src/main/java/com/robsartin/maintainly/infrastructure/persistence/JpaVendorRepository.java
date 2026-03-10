package com.robsartin.maintainly.infrastructure.persistence;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import com.robsartin.maintainly.domain.model.Vendor;
import com.robsartin.maintainly.domain.port.out.VendorRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface JpaVendorRepository
        extends JpaRepository<Vendor, UUID>,
        VendorRepository {

    @Override
    List<Vendor> findByOrganizationId(
            UUID organizationId);

    @Override
    Optional<Vendor> findByIdAndOrganizationId(
            UUID id, UUID organizationId);
}
