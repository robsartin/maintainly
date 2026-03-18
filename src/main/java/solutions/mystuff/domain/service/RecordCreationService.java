package solutions.mystuff.domain.service;

import java.math.BigDecimal;
import java.util.UUID;

import solutions.mystuff.domain.model.Item;
import solutions.mystuff.domain.model.ServiceCompletion;
import solutions.mystuff.domain.model.ServiceRecord;
import solutions.mystuff.domain.model.ServiceSchedule;
import solutions.mystuff.domain.port.in.RecordCreation;
import solutions.mystuff.domain.port.out
        .ServiceRecordRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

/**
 * Validates input and creates service records for completed maintenance work.
 *
 * <p>Enforces a required summary (max 250 chars) and optional technician
 * name (max 200 chars) before persisting via the repository.
 *
 * <div class="mermaid">
 * sequenceDiagram
 *     Controller->>RecordCreationService: createRecord(...)
 *     RecordCreationService->>RecordCreationService: validateSummary / validateTechName
 *     RecordCreationService->>ServiceRecordRepository: save(record)
 * </div>
 *
 * @see RecordCreation
 * @see ServiceRecordRepository
 */
@Service
public class RecordCreationService
        implements RecordCreation {

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
        if (completion.techName() != null
                && !completion.techName().isBlank()) {
            record.setTechnicianName(
                    completion.techName().trim());
        }
        record.setCost(completion.cost() != null
                ? completion.cost() : BigDecimal.ZERO);
        recordRepo.save(record);
        log.info("Saved service record for item {}",
                item.getId());
    }

    private void validateSummary(String summary) {
        if (summary == null || summary.isBlank()) {
            throw new IllegalArgumentException(
                    "Summary is required");
        }
        if (summary.trim().length() > MAX_SUMMARY) {
            throw new IllegalArgumentException(
                    "Summary exceeds maximum length of "
                            + MAX_SUMMARY);
        }
    }

    private void validateTechName(String techName) {
        if (techName != null
                && techName.trim().length() > MAX_TECH) {
            throw new IllegalArgumentException(
                    "Technician exceeds maximum length"
                            + " of " + MAX_TECH);
        }
    }
}
