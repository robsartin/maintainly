package solutions.mystuff.domain.port.in;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import solutions.mystuff.domain.model.ItemCostSummary;

/**
 * Inbound port for querying maintenance cost summaries.
 *
 * @see solutions.mystuff.domain.model.ItemCostSummary
 */
public interface CostQuery {

    /** Total spend for the organization in the given year. */
    BigDecimal totalSpendForYear(UUID orgId, int year);

    /** Total spend for the organization across all time. */
    BigDecimal totalSpendAllTime(UUID orgId);

    /** Top items by total cost, limited to the given count. */
    List<ItemCostSummary> topItemsByCost(
            UUID orgId, int limit);
}
