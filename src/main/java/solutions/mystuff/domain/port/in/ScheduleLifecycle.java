package solutions.mystuff.domain.port.in;

import java.time.LocalDate;
import java.util.UUID;

import solutions.mystuff.domain.model.FrequencyUnit;
import solutions.mystuff.domain.model.ServiceSchedule;
import solutions.mystuff.domain.model.Vendor;

public interface ScheduleLifecycle {

    ServiceSchedule createSchedule(UUID orgId,
            UUID itemId, String serviceType,
            Vendor vendor, LocalDate nextDueDate,
            int frequencyInterval,
            FrequencyUnit frequencyUnit);

    ServiceSchedule completeSchedule(UUID scheduleId,
            UUID orgId, Vendor vendor, String summary,
            LocalDate serviceDate, String techName);

    ServiceSchedule skipSchedule(
            UUID scheduleId, UUID orgId);

    ServiceSchedule editSchedule(UUID scheduleId,
            UUID orgId, String serviceType,
            LocalDate nextDueDate,
            int frequencyInterval,
            FrequencyUnit frequencyUnit,
            Vendor vendor);

    void deactivateSchedule(
            UUID scheduleId, UUID orgId);
}
