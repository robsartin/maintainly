package solutions.mystuff.domain.port.in;

import java.time.LocalDate;
import java.util.UUID;

import solutions.mystuff.domain.model.FrequencyUnit;
import solutions.mystuff.domain.model.ServiceSchedule;
import solutions.mystuff.domain.model.Vendor;

/**
 * Inbound port for managing the full lifecycle of service schedules.
 *
 * <div class="mermaid">
 * classDiagram
 *     class ScheduleLifecycle {
 *         <<interface>>
 *         +createSchedule(UUID, UUID, String, Vendor, LocalDate, int, FrequencyUnit) ServiceSchedule
 *         +completeSchedule(UUID, UUID, Vendor, String, LocalDate, String) ServiceSchedule
 *         +skipSchedule(UUID, UUID) ServiceSchedule
 *         +editSchedule(UUID, UUID, String, LocalDate, int, FrequencyUnit, Vendor) ServiceSchedule
 *         +deactivateSchedule(UUID, UUID) void
 *     }
 *     ScheduleLifecycleService ..|> ScheduleLifecycle
 * </div>
 *
 * @see solutions.mystuff.domain.model.ServiceSchedule
 */
public interface ScheduleLifecycle {

    /** Create a new recurring service schedule for an item. */
    ServiceSchedule createSchedule(UUID orgId,
            UUID itemId, String serviceType,
            Vendor vendor, LocalDate nextDueDate,
            int frequencyInterval,
            FrequencyUnit frequencyUnit);

    /** Mark a schedule as completed and log the service. */
    ServiceSchedule completeSchedule(UUID scheduleId,
            UUID orgId, Vendor vendor, String summary,
            LocalDate serviceDate, String techName);

    /** Skip the current occurrence and advance the due date. */
    ServiceSchedule skipSchedule(
            UUID scheduleId, UUID orgId);

    /** Update the configuration of an existing schedule. */
    ServiceSchedule editSchedule(UUID scheduleId,
            UUID orgId, String serviceType,
            LocalDate nextDueDate,
            int frequencyInterval,
            FrequencyUnit frequencyUnit,
            Vendor vendor);

    /** Deactivate a schedule so it no longer triggers. */
    void deactivateSchedule(
            UUID scheduleId, UUID orgId);
}
