package solutions.mystuff.application.web.api;

import java.time.LocalDate;
import java.util.UUID;

import solutions.mystuff.domain.model.FrequencyUnit;
import solutions.mystuff.domain.model.ServiceSchedule;

/**
 * JSON response DTO for a service schedule.
 *
 * <div class="mermaid">
 * classDiagram
 *     class ScheduleResponse {
 *         UUID id
 *         String serviceType
 *         LocalDate nextDueDate
 *     }
 *     ScheduleResponse ..&gt; ServiceSchedule : from
 * </div>
 *
 * @see ScheduleApiController
 */
public record ScheduleResponse(
        UUID id,
        String itemName,
        UUID itemId,
        String serviceType,
        String vendorName,
        LocalDate nextDueDate,
        LocalDate lastCompletedDate,
        int frequencyInterval,
        FrequencyUnit frequencyUnit,
        boolean active) {

    /** Creates a response from a domain schedule. */
    public static ScheduleResponse from(
            ServiceSchedule s) {
        return new ScheduleResponse(
                s.getId(),
                s.getItem() != null
                        ? s.getItem().getName() : null,
                s.getItem() != null
                        ? s.getItem().getId() : null,
                s.getServiceType(),
                s.getPreferredVendor() != null
                        ? s.getPreferredVendor().getName()
                        : null,
                s.getNextDueDate(),
                s.getLastCompletedDate(),
                s.getFrequencyInterval(),
                s.getFrequencyUnit(),
                s.isActive());
    }
}
