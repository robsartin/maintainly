package solutions.mystuff.domain.model;

import java.util.UUID;

/**
 * Read-only projection of per-facility dashboard statistics.
 *
 * <div class="mermaid">
 * classDiagram
 *     class FacilitySummary {
 *         UUID facilityId
 *         String facilityName
 *         long itemCount
 *         long overdueCount
 *     }
 * </div>
 *
 * @see solutions.mystuff.domain.port.in.DashboardQuery
 */
public record FacilitySummary(
        UUID facilityId,
        String facilityName,
        long itemCount,
        long overdueCount) {
}
