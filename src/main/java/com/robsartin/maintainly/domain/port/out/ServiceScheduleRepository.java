package com.robsartin.maintainly.domain.port.out;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import com.robsartin.maintainly.domain.model.ServiceSchedule;

public interface ServiceScheduleRepository {

    List<ServiceSchedule>
            findByOrganizationIdOrderByNextDueDate(
                    UUID organizationId);

    List<ServiceSchedule> findActiveByOrganizationId(
            UUID organizationId);

    Optional<ServiceSchedule> findByIdAndOrganizationId(
            UUID id, UUID organizationId);

    List<ServiceSchedule>
            findByItemIdAndOrganizationId(
                    UUID itemId, UUID organizationId);

    ServiceSchedule save(ServiceSchedule schedule);
}
