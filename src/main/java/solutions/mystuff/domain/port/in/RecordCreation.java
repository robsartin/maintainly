package solutions.mystuff.domain.port.in;

import java.time.LocalDate;
import java.util.UUID;

import solutions.mystuff.domain.model.Item;
import solutions.mystuff.domain.model.ServiceSchedule;
import solutions.mystuff.domain.model.Vendor;

/**
 * Inbound port for creating service records.
 *
 * <div class="mermaid">
 * classDiagram
 *     class RecordCreation {
 *         <<interface>>
 *         +createRecord(UUID, Item, String, ServiceSchedule, Vendor, String, LocalDate, String) void
 *     }
 *     RecordCreationService ..|> RecordCreation
 * </div>
 *
 * @see solutions.mystuff.domain.model.ServiceRecord
 */
public interface RecordCreation {

    /** Create a new service record for the given item. */
    void createRecord(UUID orgId, Item item,
            String serviceType,
            ServiceSchedule schedule, Vendor vendor,
            String summary, LocalDate serviceDate,
            String techName);
}
