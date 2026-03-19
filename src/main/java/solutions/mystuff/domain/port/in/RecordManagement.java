package solutions.mystuff.domain.port.in;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

import solutions.mystuff.domain.model.ServiceRecord;

/**
 * Inbound port for updating and deleting service records.
 *
 * <p>Complements {@link RecordCreation} with edit and
 * delete capabilities. Both ports are implemented by
 * the same domain service.
 *
 * <div class="mermaid">
 * classDiagram
 *     class RecordManagement {
 *         +updateRecord(UUID, UUID, String, LocalDate, String, BigDecimal) ServiceRecord
 *         +deleteRecord(UUID, UUID) void
 *     }
 *     ServiceRecordService ..|> RecordManagement
 * </div>
 *
 * @see RecordCreation
 * @see solutions.mystuff.domain.model.ServiceRecord
 */
public interface RecordManagement {

    /**
     * Update editable fields on an existing service record.
     *
     * @param orgId    the organization scope
     * @param recordId the record to update
     * @param summary  what was done (required)
     * @param serviceDate date of service (required)
     * @param techName technician name (optional)
     * @param cost     service cost (optional)
     * @return the updated record
     */
    ServiceRecord updateRecord(UUID orgId, UUID recordId,
            String summary, LocalDate serviceDate,
            String techName, BigDecimal cost);

    /**
     * Delete a service record by ID within an org scope.
     *
     * @param orgId    the organization scope
     * @param recordId the record to delete
     */
    void deleteRecord(UUID orgId, UUID recordId);
}
