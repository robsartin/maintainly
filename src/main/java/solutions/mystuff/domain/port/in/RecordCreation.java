package solutions.mystuff.domain.port.in;

import java.util.UUID;

import solutions.mystuff.domain.model.Item;
import solutions.mystuff.domain.model.ServiceCompletion;
import solutions.mystuff.domain.model.ServiceSchedule;

/**
 * Inbound port for creating service records.
 *
 * <div class="mermaid">
 * classDiagram
 *     class RecordCreation {
 *         +createRecord(UUID, Item, ServiceSchedule, ServiceCompletion) void
 *     }
 *     ServiceRecordService ..|> RecordCreation
 * </div>
 *
 * @see solutions.mystuff.domain.model.ServiceRecord
 */
public interface RecordCreation {

    /**
     * Create a new service record (visit) for the given item.
     *
     * <p>When a schedule is provided, the service type is
     * extracted from it. For one-off records (no schedule),
     * the service type is null.
     *
     * <p>Vendor and techName are optional but encouraged for
     * traceability. Unlike schedules, visits do not require
     * a vendor.
     */
    void createRecord(UUID orgId, Item item,
            ServiceSchedule schedule,
            ServiceCompletion completion);
}
