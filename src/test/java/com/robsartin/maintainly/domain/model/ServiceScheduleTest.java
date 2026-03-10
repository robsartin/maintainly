package com.robsartin.maintainly.domain.model;

import java.time.LocalDate;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DisplayName("ServiceSchedule")
class ServiceScheduleTest {

    @Test
    @DisplayName("should have defaults")
    void shouldHaveDefaults() {
        ServiceSchedule s = new ServiceSchedule();
        assertNull(s.getOrganizationId());
        assertNull(s.getItem());
        assertNull(s.getServiceType());
        assertNull(s.getPreferredVendor());
        assertNull(s.getFrequencyUnit());
        assertNull(s.getFrequencyInterval());
        assertNull(s.getFirstDueDate());
        assertNull(s.getNextDueDate());
        assertNull(s.getLastCompletedDate());
        assertTrue(s.isActive());
        assertNull(s.getNotes());
    }

    @Test
    @DisplayName("should set and get all fields")
    void shouldSetAndGetFields() {
        ServiceSchedule s = new ServiceSchedule();
        var orgId = UuidV7.generate();
        Item item = new Item();
        ServiceType type = new ServiceType();
        Vendor vendor = new Vendor();
        s.setOrganizationId(orgId);
        s.setItem(item);
        s.setServiceType(type);
        s.setPreferredVendor(vendor);
        s.setFrequencyUnit(FrequencyUnit.months);
        s.setFrequencyInterval(6);
        s.setFirstDueDate(LocalDate.of(2026, 1, 1));
        s.setNextDueDate(LocalDate.of(2026, 7, 1));
        s.setLastCompletedDate(
                LocalDate.of(2026, 1, 1));
        s.setActive(false);
        s.setNotes("Semi-annual");
        assertEquals(orgId, s.getOrganizationId());
        assertEquals(item, s.getItem());
        assertEquals(type, s.getServiceType());
        assertEquals(vendor, s.getPreferredVendor());
        assertEquals(FrequencyUnit.months,
                s.getFrequencyUnit());
        assertEquals(6, s.getFrequencyInterval());
        assertEquals(LocalDate.of(2026, 1, 1),
                s.getFirstDueDate());
        assertEquals(LocalDate.of(2026, 7, 1),
                s.getNextDueDate());
        assertEquals(LocalDate.of(2026, 1, 1),
                s.getLastCompletedDate());
        assertFalse(s.isActive());
        assertEquals("Semi-annual", s.getNotes());
    }

    @Test
    @DisplayName("should advance next due by days")
    void shouldAdvanceByDays() {
        ServiceSchedule s = new ServiceSchedule();
        s.setFrequencyUnit(FrequencyUnit.days);
        s.setFrequencyInterval(14);
        s.advanceNextDueDate(LocalDate.of(2026, 3, 10));
        assertEquals(LocalDate.of(2026, 3, 24),
                s.getNextDueDate());
        assertEquals(LocalDate.of(2026, 3, 10),
                s.getLastCompletedDate());
    }

    @Test
    @DisplayName("should advance next due by weeks")
    void shouldAdvanceByWeeks() {
        ServiceSchedule s = new ServiceSchedule();
        s.setFrequencyUnit(FrequencyUnit.weeks);
        s.setFrequencyInterval(2);
        s.advanceNextDueDate(LocalDate.of(2026, 3, 10));
        assertEquals(LocalDate.of(2026, 3, 24),
                s.getNextDueDate());
    }

    @Test
    @DisplayName("should advance next due by months")
    void shouldAdvanceByMonths() {
        ServiceSchedule s = new ServiceSchedule();
        s.setFrequencyUnit(FrequencyUnit.months);
        s.setFrequencyInterval(6);
        s.advanceNextDueDate(LocalDate.of(2026, 3, 10));
        assertEquals(LocalDate.of(2026, 9, 10),
                s.getNextDueDate());
    }

    @Test
    @DisplayName("should advance next due by years")
    void shouldAdvanceByYears() {
        ServiceSchedule s = new ServiceSchedule();
        s.setFrequencyUnit(FrequencyUnit.years);
        s.setFrequencyInterval(1);
        s.advanceNextDueDate(LocalDate.of(2026, 3, 10));
        assertEquals(LocalDate.of(2027, 3, 10),
                s.getNextDueDate());
    }
}
