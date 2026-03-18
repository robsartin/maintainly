package solutions.mystuff.domain.service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import solutions.mystuff.domain.model.ItemCostSummary;
import solutions.mystuff.domain.port.in.CostQuery;
import solutions.mystuff.domain.port.out
        .ServiceRecordRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation
        .Transactional;

/**
 * Queries aggregated maintenance cost data.
 *
 * <div class="mermaid">
 * sequenceDiagram
 *     Controller->>CostQueryService: totalSpendForYear
 *     CostQueryService->>ServiceRecordRepository: sumCostByOrganizationAndYear
 *     ServiceRecordRepository-->>CostQueryService: BigDecimal
 *     CostQueryService-->>Controller: BigDecimal
 * </div>
 *
 * @see CostQuery
 * @see ServiceRecordRepository
 */
@Service
public class CostQueryService implements CostQuery {

    private final ServiceRecordRepository recordRepo;

    /** Creates a service backed by the given repository. */
    public CostQueryService(
            ServiceRecordRepository recordRepo) {
        this.recordRepo = recordRepo;
    }

    @Override
    @Transactional(readOnly = true)
    public BigDecimal totalSpendForYear(
            UUID orgId, int year) {
        LocalDate from = LocalDate.of(year, 1, 1);
        LocalDate to = LocalDate.of(year + 1, 1, 1);
        return recordRepo
                .sumCostByOrganizationAndDateRange(
                        orgId, from, to);
    }

    @Override
    @Transactional(readOnly = true)
    public BigDecimal totalSpendAllTime(UUID orgId) {
        return recordRepo
                .sumCostByOrganization(orgId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ItemCostSummary> topItemsByCost(
            UUID orgId, int limit) {
        return recordRepo
                .findTopItemsByCost(orgId, limit);
    }
}
