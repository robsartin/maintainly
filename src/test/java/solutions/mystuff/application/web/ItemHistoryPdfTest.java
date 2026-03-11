package solutions.mystuff.application.web;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;

import solutions.mystuff.domain.model.FrequencyUnit;
import solutions.mystuff.domain.model.Item;
import solutions.mystuff.domain.model.ServiceRecord;
import solutions.mystuff.domain.model.ServiceSchedule;
import solutions.mystuff.domain.model.Vendor;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletResponse;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("ItemHistoryPdf")
class ItemHistoryPdfTest {

    @Test
    @DisplayName("should generate PDF with records and"
            + " schedules")
    void shouldGenerateWithData() throws Exception {
        MockHttpServletResponse response =
                new MockHttpServletResponse();
        Vendor vendor = new Vendor();
        vendor.setName("Acme Repair");

        ServiceSchedule schedule = newSchedule();
        schedule.setPreferredVendor(vendor);
        schedule.setNextDueDate(
                LocalDate.of(2026, 4, 1));
        schedule.setLastCompletedDate(
                LocalDate.of(2026, 1, 15));

        ServiceRecord record = new ServiceRecord();
        record.setServiceDate(
                LocalDate.of(2026, 1, 15));
        record.setServiceType("Filter Change");
        record.setVendor(vendor);
        record.setDescription("Technician: John");
        record.setSummary("Replaced filter");

        Item item = buildItem();
        ItemHistoryPdf.write(response, item,
                List.of(record), List.of(schedule),
                "TestOrg", "testuser");

        assertThat(response.getContentType())
                .isEqualTo("application/pdf");
        assertThat(response.getContentAsByteArray())
                .isNotEmpty();
    }

    @Test
    @DisplayName("should generate PDF with empty records"
            + " and schedules")
    void shouldGenerateEmpty() throws Exception {
        MockHttpServletResponse response =
                new MockHttpServletResponse();
        Item item = buildItem();

        ItemHistoryPdf.write(response, item,
                Collections.emptyList(),
                Collections.emptyList(),
                "TestOrg", "testuser");

        assertThat(response.getContentType())
                .isEqualTo("application/pdf");
        assertThat(response.getContentAsByteArray())
                .isNotEmpty();
    }

    @Test
    @DisplayName("should handle null vendor and null"
            + " dates on schedule")
    void shouldHandleNullVendorAndDates()
            throws Exception {
        MockHttpServletResponse response =
                new MockHttpServletResponse();
        ServiceSchedule schedule = newSchedule();
        schedule.setPreferredVendor(null);
        schedule.setNextDueDate(null);
        schedule.setLastCompletedDate(null);

        Item item = buildItem();
        ItemHistoryPdf.write(response, item,
                Collections.emptyList(),
                List.of(schedule),
                "TestOrg", "testuser");

        assertThat(response.getContentAsByteArray())
                .isNotEmpty();
    }

    @Test
    @DisplayName("should handle null fields on records")
    void shouldHandleNullRecordFields()
            throws Exception {
        MockHttpServletResponse response =
                new MockHttpServletResponse();
        ServiceRecord record = new ServiceRecord();
        record.setServiceDate(null);
        record.setServiceType(null);
        record.setVendor(null);
        record.setDescription(null);
        record.setSummary(null);

        Item item = buildItem();
        ItemHistoryPdf.write(response, item,
                List.of(record),
                Collections.emptyList(),
                "TestOrg", "testuser");

        assertThat(response.getContentAsByteArray())
                .isNotEmpty();
    }

    @Test
    @DisplayName("should handle description without"
            + " technician prefix")
    void shouldHandleNonTechDescription()
            throws Exception {
        MockHttpServletResponse response =
                new MockHttpServletResponse();
        ServiceRecord record = new ServiceRecord();
        record.setServiceDate(
                LocalDate.of(2026, 2, 1));
        record.setServiceType("Inspection");
        record.setDescription("General notes");
        record.setSummary("Looked good");

        Item item = buildItem();
        ItemHistoryPdf.write(response, item,
                List.of(record),
                Collections.emptyList(),
                "TestOrg", "testuser");

        assertThat(response.getContentAsByteArray())
                .isNotEmpty();
    }

    @Test
    @DisplayName("should handle null item detail fields")
    void shouldHandleNullItemFields()
            throws Exception {
        MockHttpServletResponse response =
                new MockHttpServletResponse();
        Item item = new Item();
        item.setName("Bare Item");

        ItemHistoryPdf.write(response, item,
                Collections.emptyList(),
                Collections.emptyList(),
                "TestOrg", "testuser");

        assertThat(response.getContentAsByteArray())
                .isNotEmpty();
    }

    @Test
    @DisplayName("should handle inactive schedule")
    void shouldHandleInactiveSchedule()
            throws Exception {
        MockHttpServletResponse response =
                new MockHttpServletResponse();
        ServiceSchedule schedule = newSchedule();
        schedule.setActive(false);

        Item item = buildItem();
        ItemHistoryPdf.write(response, item,
                Collections.emptyList(),
                List.of(schedule),
                "TestOrg", "testuser");

        assertThat(response.getContentAsByteArray())
                .isNotEmpty();
    }

    private static Item buildItem() {
        Item item = new Item();
        item.setName("Test Item");
        item.setLocation("Building A");
        item.setManufacturer("Acme");
        item.setModelName("Model X");
        item.setSerialNumber("SN-123");
        return item;
    }

    private static ServiceSchedule newSchedule() {
        ServiceSchedule s = new ServiceSchedule();
        s.setServiceType("Filter Change");
        s.setFrequencyUnit(FrequencyUnit.months);
        s.setFrequencyInterval(3);
        s.setActive(true);
        return s;
    }
}
