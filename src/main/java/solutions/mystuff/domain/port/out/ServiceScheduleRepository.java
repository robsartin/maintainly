package solutions.mystuff.domain.port.out;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import solutions.mystuff.domain.model.PageResult;
import solutions.mystuff.domain.model.ServiceSchedule;

/**
 * Outbound port for querying and persisting service schedules.
 *
 * <div class="mermaid">
 * classDiagram
 *     class ServiceScheduleRepository {
 *         +findByOrganizationIdOrderByNextDueDate(UUID) List~ServiceSchedule~
 *         +findActiveByOrganizationId(UUID) List~ServiceSchedule~
 *         +findActiveByOrganizationId(UUID, int, int) PageResult~ServiceSchedule~
 *         +findByIdAndOrganizationId(UUID, UUID) Optional~ServiceSchedule~
 *         +findByItemIdAndOrganizationId(UUID, UUID) List~ServiceSchedule~
 *         +save(ServiceSchedule) ServiceSchedule
 *     }
 *     JpaScheduleRepositoryAdapter ..|> ServiceScheduleRepository
 * </div>
 *
 * @see solutions.mystuff.domain.model.ServiceSchedule
 */
public interface ServiceScheduleRepository {

    /** Find all schedules for an organization ordered by due date. */
    List<ServiceSchedule>
            findByOrganizationIdOrderByNextDueDate(
                    UUID organizationId);

    /** Find all active schedules for an organization. */
    List<ServiceSchedule> findActiveByOrganizationId(
            UUID organizationId);

    /** Find a page of active schedules for an organization. */
    PageResult<ServiceSchedule>
            findActiveByOrganizationId(
                    UUID organizationId,
                    int page, int size);

    /** Find a single schedule by ID scoped to an organization. */
    Optional<ServiceSchedule> findByIdAndOrganizationId(
            UUID id, UUID organizationId);

    /** Find all schedules for a specific item in an organization. */
    List<ServiceSchedule>
            findByItemIdAndOrganizationId(
                    UUID itemId, UUID organizationId);

    /** Persist a new or updated service schedule. */
    ServiceSchedule save(ServiceSchedule schedule);
}
