package com.robsartin.maintainly.domain.port.out;

import java.util.List;
import java.util.UUID;

import com.robsartin.maintainly.domain.model.ServiceRequest;

public interface ServiceRequestRepository {

    List<ServiceRequest> findByPropertyId(UUID propertyId);

    ServiceRequest save(ServiceRequest serviceRequest);

    void markCompleted(UUID id);
}
