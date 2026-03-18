package solutions.mystuff.domain.model;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Value object carrying the details of a service visit.
 * Groups vendor, summary, date, technician, and cost.
 *
 * <div class="mermaid">
 * classDiagram
 *     class ServiceCompletion {
 *         Vendor vendor
 *         String summary
 *         LocalDate serviceDate
 *         String techName
 *         BigDecimal cost
 *     }
 *     ScheduleLifecycle ..&gt; ServiceCompletion : uses
 *     RecordCreation ..&gt; ServiceCompletion : uses
 * </div>
 *
 * @see ServiceRecord
 */
public record ServiceCompletion(
        Vendor vendor,
        String summary,
        LocalDate serviceDate,
        String techName,
        BigDecimal cost) {
}
