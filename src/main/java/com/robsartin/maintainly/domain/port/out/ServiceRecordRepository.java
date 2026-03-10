package com.robsartin.maintainly.domain.port.out;

import java.util.List;
import java.util.UUID;

import com.robsartin.maintainly.domain.model.ServiceRecord;

public interface ServiceRecordRepository {

    List<ServiceRecord> findByItemIdAndOrganizationId(
            UUID itemId, UUID organizationId);

    ServiceRecord save(ServiceRecord record);
}
