package solutions.mystuff.domain.service;

import java.util.List;
import java.util.UUID;

import solutions.mystuff.domain.model.Facility;
import solutions.mystuff.domain.model.UuidV7;
import solutions.mystuff.domain.port.out.FacilityRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Unit tests for {@link FacilityQueryService}.
 *
 * <div class="mermaid">
 * sequenceDiagram
 *     Test->>FacilityQueryService: findAllFacilities(orgId)
 *     FacilityQueryService->>FacilityRepository: findByOrganizationId(orgId)
 *     FacilityRepository-->>Test: result
 * </div>
 */
@DisplayName("FacilityQueryService")
class FacilityQueryServiceTest {

    private final FacilityRepository facilityRepo =
            mock(FacilityRepository.class);
    private final FacilityQueryService service =
            new FacilityQueryService(facilityRepo);

    private final UUID orgId = UuidV7.generate();

    @Test
    @DisplayName("should delegate to repository")
    void shouldDelegateToRepository() {
        Facility f = new Facility();
        f.setName("Main Office");
        when(facilityRepo.findByOrganizationId(orgId))
                .thenReturn(List.of(f));
        List<Facility> result =
                service.findAllFacilities(orgId);
        assertEquals(1, result.size());
        assertEquals("Main Office",
                result.get(0).getName());
    }

    @Test
    @DisplayName("should return empty list when none exist")
    void shouldReturnEmptyWhenNoneFacilitiesExist() {
        when(facilityRepo.findByOrganizationId(orgId))
                .thenReturn(List.of());
        assertTrue(service.findAllFacilities(orgId)
                .isEmpty());
    }
}
