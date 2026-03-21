package solutions.mystuff.domain.port.in;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import solutions.mystuff.domain.model.FacilitySummary;
import solutions.mystuff.domain.model.ServiceRecord;

/**
 * Inbound port for dashboard summary queries.
 *
 * <p>Methods accepting a {@code facilityId} scope results
 * to items assigned to that facility. Org-wide methods
 * aggregate across all facilities.
 *
 * <div class="mermaid">
 * classDiagram
 *     class DashboardQuery {
 *         +countOverdueSchedules(UUID, LocalDate) long
 *         +countDueSoonSchedules(UUID, LocalDate, LocalDate) long
 *         +countItems(UUID) long
 *         +findRecentRecords(UUID, int) List
 *         +countOverdueByFacility(UUID, UUID, LocalDate) long
 *         +countDueSoonByFacility(UUID, UUID, LocalDate, LocalDate) long
 *         +countItemsByFacility(UUID, UUID) long
 *         +findRecentByFacility(UUID, UUID, int) List
 *         +findFacilitySummaries(UUID, LocalDate) List
 *     }
 *     DashboardQueryService ..|> DashboardQuery
 * </div>
 *
 * @see solutions.mystuff.domain.model.ServiceRecord
 * @see solutions.mystuff.domain.model.ServiceSchedule
 * @see solutions.mystuff.domain.model.FacilitySummary
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

    /** Count overdue schedules scoped to a facility. */
    long countOverdueByFacility(
            UUID orgId, UUID facilityId, LocalDate today);

    /** Count due-soon schedules scoped to a facility. */
    long countDueSoonByFacility(
            UUID orgId, UUID facilityId,
            LocalDate from, LocalDate to);

    /** Count items assigned to a facility. */
    long countItemsByFacility(
            UUID orgId, UUID facilityId);

    /** Find recent records scoped to a facility. */
    List<ServiceRecord> findRecentByFacility(
            UUID orgId, UUID facilityId, int limit);

    /** Per-facility breakdown with item and overdue counts. */
    List<FacilitySummary> findFacilitySummaries(
            UUID orgId, LocalDate today);
}
