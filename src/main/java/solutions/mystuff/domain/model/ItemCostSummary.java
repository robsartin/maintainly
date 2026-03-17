package solutions.mystuff.domain.model;

import java.math.BigDecimal;
import java.util.UUID;

/**
 * Read-only projection of total cost per item.
 *
 * <div class="mermaid">
 * classDiagram
 *     class ItemCostSummary {
 *         UUID itemId
 *         String itemName
 *         BigDecimal totalCost
 *     }
 * </div>
 *
 * @see solutions.mystuff.domain.port.in.CostQuery
 */
public record ItemCostSummary(
        UUID itemId,
        String itemName,
        BigDecimal totalCost) {
}
