package com.robsartin.maintainly.infrastructure.persistence;

import java.util.List;
import java.util.UUID;

import com.robsartin.maintainly.domain.model.ServiceRequest;
import com.robsartin.maintainly.domain.port.out.ServiceRequestRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public interface JpaServiceRequestRepository
        extends JpaRepository<ServiceRequest, UUID>,
        ServiceRequestRepository {

    @Override
    List<ServiceRequest> findByPropertyId(UUID propertyId);

    @Override
    @Modifying
    @Transactional
    @Query("UPDATE ServiceRequest sr "
            + "SET sr.completed = true WHERE sr.id = :id")
    void markCompleted(@Param("id") UUID id);
}
