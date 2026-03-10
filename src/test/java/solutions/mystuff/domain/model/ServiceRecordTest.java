package solutions.mystuff.domain.model;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

@DisplayName("ServiceRecord")
class ServiceRecordTest {

    @Test
    @DisplayName("should have null defaults")
    void shouldHaveNullDefaults() {
        ServiceRecord r = new ServiceRecord();
        assertNull(r.getOrganizationId());
        assertNull(r.getItem());
        assertNull(r.getServiceType());
        assertNull(r.getServiceSchedule());
        assertNull(r.getVendor());
        assertNull(r.getDataEntryTimestamp());
        assertNull(r.getServiceDate());
        assertNull(r.getSummary());
        assertNull(r.getDescription());
        assertNull(r.getCost());
    }

    @Test
    @DisplayName("should set and get all fields")
    void shouldSetAndGetFields() {
        ServiceRecord r = new ServiceRecord();
        var orgId = UuidV7.generate();
        Item item = new Item();
        ServiceType type = new ServiceType();
        ServiceSchedule schedule = new ServiceSchedule();
        Vendor vendor = new Vendor();
        Instant now = Instant.now();
        r.setOrganizationId(orgId);
        r.setItem(item);
        r.setServiceType(type);
        r.setServiceSchedule(schedule);
        r.setVendor(vendor);
        r.setDataEntryTimestamp(now);
        r.setServiceDate(LocalDate.of(2026, 3, 9));
        r.setSummary("Filter replaced");
        r.setDescription("Replaced HVAC filter");
        r.setCost(new BigDecimal("150.00"));
        assertEquals(orgId, r.getOrganizationId());
        assertEquals(item, r.getItem());
        assertEquals(type, r.getServiceType());
        assertEquals(schedule, r.getServiceSchedule());
        assertEquals(vendor, r.getVendor());
        assertEquals(now, r.getDataEntryTimestamp());
        assertEquals(LocalDate.of(2026, 3, 9),
                r.getServiceDate());
        assertEquals("Filter replaced", r.getSummary());
        assertEquals("Replaced HVAC filter",
                r.getDescription());
        assertEquals(new BigDecimal("150.00"),
                r.getCost());
    }

    @Test
    @DisplayName("should set data entry timestamp on create")
    void shouldSetTimestampOnCreate() {
        ServiceRecord r = new ServiceRecord();
        r.onCreate();
        assertNotNull(r.getDataEntryTimestamp());
        assertNotNull(r.getCreatedAt());
    }

    @Test
    @DisplayName("should preserve existing data entry timestamp")
    void shouldPreserveTimestamp() {
        ServiceRecord r = new ServiceRecord();
        Instant existing = Instant.parse(
                "2026-01-01T00:00:00Z");
        r.setDataEntryTimestamp(existing);
        r.onCreate();
        assertEquals(existing,
                r.getDataEntryTimestamp());
    }
}
