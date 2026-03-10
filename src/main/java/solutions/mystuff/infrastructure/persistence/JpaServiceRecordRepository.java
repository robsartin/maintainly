package solutions.mystuff.infrastructure.persistence;

import java.util.List;
import java.util.UUID;

import solutions.mystuff.domain.model.ServiceRecord;
import solutions.mystuff.domain.port.out.ServiceRecordRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface JpaServiceRecordRepository
        extends JpaRepository<ServiceRecord, UUID>,
        ServiceRecordRepository {

    @Override
    List<ServiceRecord> findByItemIdAndOrganizationId(
            UUID itemId, UUID organizationId);

    @Override
    @Query("SELECT r FROM ServiceRecord r "
            + "JOIN FETCH r.item "
            + "LEFT JOIN FETCH r.vendor "
            + "WHERE r.organizationId = :orgId "
            + "ORDER BY r.serviceDate DESC")
    List<ServiceRecord> findByOrganizationId(
            @Param("orgId") UUID organizationId);
}
