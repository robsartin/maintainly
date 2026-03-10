package com.robsartin.maintainly.infrastructure.persistence;

import java.util.List;
import java.util.UUID;

import com.robsartin.maintainly.domain.model.ServiceRecord;
import com.robsartin.maintainly.domain.port.out.ServiceRecordRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface JpaServiceRecordRepository
        extends JpaRepository<ServiceRecord, UUID>,
        ServiceRecordRepository {

    @Override
    List<ServiceRecord> findByItemIdAndOrganizationId(
            UUID itemId, UUID organizationId);
}
