package solutions.mystuff.domain.port.out;

import java.util.List;
import java.util.UUID;

import solutions.mystuff.domain.model.ServiceRecord;

public interface ServiceRecordRepository {

    List<ServiceRecord> findByItemIdAndOrganizationId(
            UUID itemId, UUID organizationId);

    List<ServiceRecord> findByOrganizationId(
            UUID organizationId);

    ServiceRecord save(ServiceRecord record);
}
