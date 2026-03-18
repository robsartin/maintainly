package solutions.mystuff.domain.service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;

import solutions.mystuff.domain.model.FrequencyUnit;
import solutions.mystuff.domain.model.Item;
import solutions.mystuff.domain.model.NotFoundException;
import solutions.mystuff.domain.model.ServiceSchedule;
import solutions.mystuff.domain.model.Vendor;
import solutions.mystuff.domain.port.in.RecordCreation;
import solutions.mystuff.domain.port.in.ScheduleLifecycle;
import solutions.mystuff.domain.port.out.ItemRepository;
import solutions.mystuff.domain.port.out
        .ServiceScheduleRepository;
import solutions.mystuff.domain.port.in.ItemQuery;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation
        .Transactional;

/**
 * Manages the full lifecycle of service schedules within a transaction.
 *
 * <p>Supports creating, completing, skipping, editing, and deactivating
 * schedules. On completion, delegates to {@link RecordCreation} to persist
 * a service record and advances the next due date.
 *
 * <div class="mermaid">
 * sequenceDiagram
 *     Controller->>ScheduleLifecycleService: completeSchedule(...)
 *     ScheduleLifecycleService->>ScheduleLifecycleService: validate / findSchedule
 *     ScheduleLifecycleService->>RecordCreation: createRecord(...)
 *     ScheduleLifecycleService->>ServiceScheduleRepository: save(schedule)
 * </div>
 *
 * @see ScheduleLifecycle
 * @see RecordCreation
 * @see ServiceScheduleRepository
 */
@Service
public class ScheduleLifecycleService
        implements ScheduleLifecycle {

    private static final Logger log =
            LoggerFactory.getLogger(
                    ScheduleLifecycleService.class);
    private static final int MAX_TYPE = 150;

    private final ServiceScheduleRepository scheduleRepo;
    private final ItemRepository itemRepo;
    private final RecordCreation recordCreation;
    private final ItemQuery itemQuery;

    public ScheduleLifecycleService(
            ServiceScheduleRepository scheduleRepo,
            ItemRepository itemRepo,
            RecordCreation recordCreation,
            ItemQuery itemQuery) {
        this.scheduleRepo = scheduleRepo;
        this.itemRepo = itemRepo;
        this.recordCreation = recordCreation;
        this.itemQuery = itemQuery;
    }

    /** Creates a new service schedule for the given item. */
    @Override
    @Transactional
    public ServiceSchedule createSchedule(
            UUID orgId, UUID itemId,
            String serviceType, Vendor vendor,
            LocalDate nextDueDate,
            int frequencyInterval,
            FrequencyUnit frequencyUnit) {
        validateScheduleFields(serviceType,
                frequencyInterval);
        requireVendor(vendor);
        Item item = findItem(itemId, orgId);
        ServiceSchedule sched = new ServiceSchedule();
        sched.setOrganizationId(orgId);
        sched.setItem(item);
        sched.setServiceType(serviceType.trim());
        sched.setPreferredVendor(vendor);
        sched.setFrequencyUnit(frequencyUnit);
        sched.setFrequencyInterval(frequencyInterval);
        sched.setFirstDueDate(nextDueDate);
        sched.setNextDueDate(nextDueDate);
        ServiceSchedule saved =
                scheduleRepo.save(sched);
        log.info("Created schedule for item {}",
                itemId);
        return saved;
    }

    /** Records completion, creates a service record, and advances the due date. */
    @Override
    @Transactional
    public ServiceSchedule completeSchedule(
            UUID scheduleId, UUID orgId,
            Vendor vendor, String summary,
            LocalDate serviceDate, String techName,
            BigDecimal cost) {
        ServiceSchedule sched =
                findSchedule(scheduleId, orgId);
        recordCreation.createRecord(orgId,
                sched.getItem(),
                sched.getServiceType(), sched,
                vendor, summary, serviceDate,
                techName, cost);
        sched.advanceNextDueDate(serviceDate);
        ServiceSchedule saved =
                scheduleRepo.save(sched);
        log.info("Completed schedule {}", scheduleId);
        return saved;
    }

    /** Skips the current occurrence and advances the due date. */
    @Override
    @Transactional
    public ServiceSchedule skipSchedule(
            UUID scheduleId, UUID orgId) {
        ServiceSchedule sched =
                findSchedule(scheduleId, orgId);
        LocalDate current = sched.getNextDueDate();
        if (current == null) {
            current = LocalDate.now();
        }
        sched.advanceNextDueDate(current);
        sched.setLastCompletedDate(null);
        ServiceSchedule saved =
                scheduleRepo.save(sched);
        log.info("Skipped schedule {}", scheduleId);
        return saved;
    }

    /** Updates the schedule's service type, frequency, due date, and vendor. */
    @Override
    @Transactional
    public ServiceSchedule editSchedule(
            UUID scheduleId, UUID orgId,
            String serviceType, LocalDate nextDueDate,
            int frequencyInterval,
            FrequencyUnit frequencyUnit,
            Vendor vendor) {
        validateScheduleFields(serviceType,
                frequencyInterval);
        requireVendor(vendor);
        ServiceSchedule sched =
                findSchedule(scheduleId, orgId);
        sched.setServiceType(serviceType.trim());
        sched.setNextDueDate(nextDueDate);
        sched.setFrequencyInterval(frequencyInterval);
        sched.setFrequencyUnit(frequencyUnit);
        sched.setPreferredVendor(vendor);
        ServiceSchedule saved =
                scheduleRepo.save(sched);
        log.info("Updated schedule {}", scheduleId);
        return saved;
    }

    /** Marks a schedule as inactive so it no longer generates due dates. */
    @Override
    @Transactional
    public void deactivateSchedule(
            UUID scheduleId, UUID orgId) {
        ServiceSchedule sched =
                findSchedule(scheduleId, orgId);
        sched.setActive(false);
        scheduleRepo.save(sched);
        log.info("Deactivated schedule {}",
                scheduleId);
    }

    @Override
    @Transactional
    public void completeNextForItem(
            UUID orgId, UUID itemId, Vendor vendor,
            String summary, LocalDate serviceDate,
            String techName, BigDecimal cost) {
        List<ServiceSchedule> schedules =
                itemQuery.findSchedulesByItem(
                        itemId, orgId);
        ServiceSchedule next = schedules.stream()
                .filter(ServiceSchedule::isActive)
                .min(Comparator.comparing(
                        s -> s.getNextDueDate() != null
                                ? s.getNextDueDate()
                                : LocalDate.MAX))
                .orElse(null);
        if (next != null) {
            completeSchedule(next.getId(), orgId,
                    vendor, summary, serviceDate,
                    techName, cost);
        } else {
            Item item = findItem(itemId, orgId);
            recordCreation.createRecord(orgId, item,
                    null, null, vendor, summary,
                    serviceDate, techName, cost);
        }
    }

    private ServiceSchedule findSchedule(
            UUID scheduleId, UUID orgId) {
        return scheduleRepo
                .findByIdAndOrganizationId(
                        scheduleId, orgId)
                .orElseThrow(() ->
                        new NotFoundException(
                                "Schedule not found"));
    }

    private Item findItem(UUID itemId, UUID orgId) {
        return itemRepo
                .findByIdAndOrganizationId(
                        itemId, orgId)
                .orElseThrow(() ->
                        new NotFoundException(
                                "Item not found"));
    }

    private void requireVendor(Vendor vendor) {
        if (vendor == null) {
            throw new IllegalArgumentException(
                    "Vendor is required for schedules");
        }
    }

    private void validateScheduleFields(
            String serviceType,
            int frequencyInterval) {
        if (serviceType == null
                || serviceType.isBlank()) {
            throw new IllegalArgumentException(
                    "Service type is required");
        }
        if (serviceType.trim().length() > MAX_TYPE) {
            throw new IllegalArgumentException(
                    "Service type exceeds maximum"
                            + " length of " + MAX_TYPE);
        }
        if (frequencyInterval < 1) {
            throw new IllegalArgumentException(
                    "Frequency interval must be"
                            + " at least 1");
        }
    }
}
