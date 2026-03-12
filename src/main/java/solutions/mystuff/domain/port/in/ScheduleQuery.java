package solutions.mystuff.domain.port.in;

import java.util.List;
import java.util.UUID;

import solutions.mystuff.domain.model.PageResult;
import solutions.mystuff.domain.model.ServiceSchedule;

/**
 * Inbound port for read-only schedule queries.
 *
 * <div class="mermaid">
 * classDiagram
 *     class ScheduleQuery {
 *         +findActiveByOrganization(UUID, int, int) PageResult
 *         +findAllActiveByOrganization(UUID) List
 *     }
 *     ScheduleQueryService ..|> ScheduleQuery
 * </div>
 *
 * @see solutions.mystuff.domain.model.ServiceSchedule
 */
public interface ScheduleQuery {

    /** Find a page of active schedules for an organization. */
    PageResult<ServiceSchedule> findActiveByOrganization(
            UUID orgId, int page, int size);

    /** Find all active schedules for an organization. */
    List<ServiceSchedule> findAllActiveByOrganization(
            UUID orgId);
}
