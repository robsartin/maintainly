package solutions.mystuff.domain.service;

import java.time.LocalDate;
import java.util.UUID;

import solutions.mystuff.domain.model.Item;
import solutions.mystuff.domain.model.ServiceRecord;
import solutions.mystuff.domain.model.ServiceSchedule;
import solutions.mystuff.domain.model.Vendor;
import solutions.mystuff.domain.port.in.RecordCreation;
import solutions.mystuff.domain.port.out
        .ServiceRecordRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

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

    @Override
    public void createRecord(
            UUID orgId, Item item,
            String serviceType,
            ServiceSchedule schedule, Vendor vendor,
            String summary, LocalDate serviceDate,
            String techName) {
        validateSummary(summary);
        validateTechName(techName);
        ServiceRecord record = new ServiceRecord();
        record.setOrganizationId(orgId);
        record.setItem(item);
        record.setServiceType(serviceType);
        record.setServiceSchedule(schedule);
        record.setVendor(vendor);
        record.setServiceDate(serviceDate);
        record.setSummary(summary.trim());
        if (techName != null && !techName.isBlank()) {
            record.setDescription(
                    "Technician: " + techName.trim());
        }
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
