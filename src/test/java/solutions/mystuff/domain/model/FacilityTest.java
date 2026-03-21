package solutions.mystuff.domain.model;

import java.util.UUID;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Unit tests for {@link Facility}.
 *
 * <div class="mermaid">
 * classDiagram
 *     class FacilityTest
 *     FacilityTest --> Facility
 * </div>
 */
@DisplayName("Facility")
class FacilityTest {

    @Test
    @DisplayName("should store and retrieve name")
    void shouldStoreAndRetrieveName() {
        Facility facility = new Facility();
        facility.setName("Building A");
        assertEquals("Building A", facility.getName());
    }

    @Test
    @DisplayName("should default name to null")
    void shouldDefaultNameToNull() {
        Facility facility = new Facility();
        assertNull(facility.getName());
    }

    @Test
    @DisplayName("should inherit organization ownership")
    void shouldInheritOrganizationOwnership() {
        Facility facility = new Facility();
        UUID orgId = UuidV7.generate();
        facility.setOrganizationId(orgId);
        assertTrue(facility.belongsTo(orgId));
    }
}
