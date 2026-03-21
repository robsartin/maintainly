package solutions.mystuff.domain.model;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.UUID;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

@DisplayName("Item")
class ItemTest {

    @Test
    @DisplayName("should have null defaults")
    void shouldHaveNullDefaults() {
        Item item = new Item();
        assertNull(item.getOrganizationId());
        assertNull(item.getName());
        assertNull(item.getLocation());
        assertNull(item.getManufacturer());
        assertNull(item.getModelName());
        assertNull(item.getModelNumber());
        assertNull(item.getModelYear());
        assertNull(item.getSerialNumber());
        assertNull(item.getPurchaseDate());
        assertNull(item.getFacilityId());
        assertNull(item.getNotes());
        assertNotNull(item.getServiceSchedules());
        assertNotNull(item.getServiceRecords());
    }

    @Test
    @DisplayName("should set and get all fields")
    void shouldSetAndGetFields() {
        Item item = new Item();
        var orgId = UuidV7.generate();
        item.setOrganizationId(orgId);
        item.setName("Main Furnace");
        item.setLocation("Basement");
        item.setManufacturer("Carrier");
        item.setModelName("58STA");
        item.setModelNumber("58STA090-1");
        item.setModelYear(2020);
        item.setSerialNumber("ABC123");
        item.setPurchaseDate(
                LocalDate.of(2020, 6, 15));
        item.setNotes("Installed by ABC");
        UUID facId = UuidV7.generate();
        item.setFacilityId(facId);
        item.setServiceSchedules(new ArrayList<>());
        item.setServiceRecords(new ArrayList<>());
        assertEquals(orgId, item.getOrganizationId());
        assertEquals(facId, item.getFacilityId());
        assertEquals("Main Furnace", item.getName());
        assertEquals("Basement", item.getLocation());
        assertEquals("Carrier", item.getManufacturer());
        assertEquals("58STA", item.getModelName());
        assertEquals("58STA090-1",
                item.getModelNumber());
        assertEquals(2020, item.getModelYear());
        assertEquals("ABC123", item.getSerialNumber());
        assertEquals(LocalDate.of(2020, 6, 15),
                item.getPurchaseDate());
        assertEquals("Installed by ABC",
                item.getNotes());
    }
}
