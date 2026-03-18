package solutions.mystuff.domain.port.in;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import solutions.mystuff.domain.model.ServiceRecord;

/**
 * Inbound port for dashboard summary queries.
 *
 * <div class="mermaid">
 * classDiagram
 *     class DashboardQuery {
 *         +countOverdueSchedules(UUID, LocalDate) long
 *         +countDueSoonSchedules(UUID, LocalDate, LocalDate) long
 *         +countItems(UUID) long
 *         +findRecentRecords(UUID, int) List
 *     }
 *     DashboardQueryService ..|> DashboardQuery
 * </div>
 *
 * @see solutions.mystuff.domain.model.ServiceRecord
 * @see solutions.mystuff.domain.model.ServiceSchedule
 */
public interface DashboardQuery {

    /** Count active schedules whose next due date is before today. */
    long countOverdueSchedules(
            UUID orgId, LocalDate today);

    /**
     * Count active schedules whose next due date falls
     * after {@code from} and on or before {@code to}.
     */
    long countDueSoonSchedules(
            UUID orgId, LocalDate from, LocalDate to);

    /** Count all items belonging to an organization. */
    long countItems(UUID orgId);

    /** Find the most recent service records for an organization. */
    List<ServiceRecord> findRecentRecords(
            UUID orgId, int limit);
}
