package com.robsartin.maintainly.infrastructure.persistence;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import com.robsartin.maintainly.domain.model.ServiceSchedule;
import com.robsartin.maintainly.domain.port.out.ServiceScheduleRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface JpaServiceScheduleRepository
        extends JpaRepository<ServiceSchedule, UUID>,
        ServiceScheduleRepository {

    @Override
    @Query("SELECT s FROM ServiceSchedule s "
            + "JOIN FETCH s.item "
            + "JOIN FETCH s.serviceType "
            + "LEFT JOIN FETCH s.preferredVendor "
            + "WHERE s.organizationId = :orgId "
            + "ORDER BY s.nextDueDate ASC NULLS LAST")
    List<ServiceSchedule>
            findByOrganizationIdOrderByNextDueDate(
                    @Param("orgId") UUID organizationId);

    @Override
    @Query("SELECT s FROM ServiceSchedule s "
            + "JOIN FETCH s.item "
            + "JOIN FETCH s.serviceType "
            + "LEFT JOIN FETCH s.preferredVendor "
            + "WHERE s.organizationId = :orgId "
            + "AND s.active = true "
            + "ORDER BY s.nextDueDate ASC NULLS LAST")
    List<ServiceSchedule> findActiveByOrganizationId(
            @Param("orgId") UUID organizationId);

    @Override
    Optional<ServiceSchedule> findByIdAndOrganizationId(
            UUID id, UUID organizationId);
}
