package solutions.mystuff.domain.service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

import solutions.mystuff.domain.model.Item;
import solutions.mystuff.domain.model.NotFoundException;
import solutions.mystuff.domain.model.ServiceCompletion;
import solutions.mystuff.domain.model.ServiceRecord;
import solutions.mystuff.domain.model.ServiceSchedule;
import solutions.mystuff.domain.model.Validation;
import solutions.mystuff.domain.port.in.RecordCreation;
import solutions.mystuff.domain.port.in.RecordManagement;
import solutions.mystuff.domain.port.out
        .ServiceRecordRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

/**
 * Validates input and manages service records for completed
 * maintenance work: create, update, and delete.
 *
 * <p>Enforces a required summary (max 250 chars) and
 * optional technician name (max 200 chars) before
 * persisting via the repository.
 *
 * <div class="mermaid">
 * sequenceDiagram
 *     Controller->>RecordCreationService: createRecord(...)
 *     RecordCreationService->>RecordCreationService: validate
 *     RecordCreationService->>ServiceRecordRepository: save
 *     Controller->>RecordCreationService: updateRecord(...)
 *     RecordCreationService->>ServiceRecordRepository: save
 *     Controller->>RecordCreationService: deleteRecord(...)
 *     RecordCreationService->>ServiceRecordRepository: delete
 * </div>
 *
 * @see RecordCreation
 * @see RecordManagement
 * @see ServiceRecordRepository
 */
@Service
public class RecordCreationService
        implements RecordCreation, RecordManagement {

    private static final Logger log =
            LoggerFactory.getLogger(
                    RecordCreationService.class);
    private static final int MAX_SUMMARY = 250;
    private static final int MAX_TECH = 200;

    private final ServiceRecordRepository recordRepo;

    public RecordCreationService(
            ServiceRecordRepository recordRepo) {
        this.recordRepo = recordRepo;
    }

    /** Validates inputs and persists a new service record. */
    @Override
    public void createRecord(
            UUID orgId, Item item,
            ServiceSchedule schedule,
            ServiceCompletion completion) {
        validateSummary(completion.summary());
        validateTechName(completion.techName());
        String serviceType = schedule != null
                ? schedule.getServiceType() : null;
        ServiceRecord record = new ServiceRecord();
        record.setOrganizationId(orgId);
        record.setItem(item);
        record.setServiceType(serviceType);
        record.setServiceSchedule(schedule);
        record.setVendor(completion.vendor());
        record.setServiceDate(completion.serviceDate());
        record.setSummary(completion.summary().trim());
        record.setTechnicianName(
                Validation.trimOrNull(
                        completion.techName()));
        record.setCost(completion.cost() != null
                ? completion.cost() : BigDecimal.ZERO);
        recordRepo.save(record);
        log.info("Saved service record for item {}",
                item.getId());
    }

    /** Validates inputs and updates an existing record. */
    @Override
    public ServiceRecord updateRecord(
            UUID orgId, UUID recordId,
            String summary, LocalDate serviceDate,
            String techName, BigDecimal cost) {
        validateSummary(summary);
        validateTechName(techName);
        ServiceRecord record =
                requireRecord(orgId, recordId);
        record.setSummary(summary.trim());
        record.setServiceDate(serviceDate);
        record.setTechnicianName(
                Validation.trimOrNull(techName));
        record.setCost(cost != null
                ? cost : BigDecimal.ZERO);
        recordRepo.save(record);
        log.info("Updated service record {}", recordId);
        return record;
    }

    /** Deletes a service record after verifying org ownership. */
    @Override
    public void deleteRecord(
            UUID orgId, UUID recordId) {
        requireRecord(orgId, recordId);
        recordRepo.deleteByIdAndOrganizationId(
                recordId, orgId);
        log.info("Deleted service record {}", recordId);
    }

    private ServiceRecord requireRecord(
            UUID orgId, UUID recordId) {
        return recordRepo
                .findByIdAndOrganizationId(
                        recordId, orgId)
                .orElseThrow(() ->
                        new NotFoundException(
                                "Service record not found"));
    }

    private void validateSummary(String summary) {
        Validation.requireNotBlank(summary, "Summary");
        Validation.requireMaxLength(
                summary, "Summary", MAX_SUMMARY);
    }

    private void validateTechName(String techName) {
        Validation.requireMaxLength(
                techName, "Technician", MAX_TECH);
    }
}
