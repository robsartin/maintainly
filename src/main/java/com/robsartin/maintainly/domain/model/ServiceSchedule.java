package com.robsartin.maintainly.domain.model;

import java.time.LocalDate;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "service_schedules")
public class ServiceSchedule extends OrgOwnedEntity {

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "item_id", nullable = false)
    private Item item;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "service_type_id",
            nullable = false)
    private ServiceType serviceType;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "preferred_vendor_id")
    private Vendor preferredVendor;

    @Enumerated(EnumType.STRING)
    @Column(name = "frequency_unit", nullable = false,
            length = 20)
    private FrequencyUnit frequencyUnit;

    @Column(name = "frequency_interval", nullable = false)
    private Integer frequencyInterval;

    @Column(name = "first_due_date")
    private LocalDate firstDueDate;

    @Column(name = "next_due_date")
    private LocalDate nextDueDate;

    @Column(name = "last_completed_date")
    private LocalDate lastCompletedDate;

    @Column(nullable = false)
    private boolean active = true;

    @Column(columnDefinition = "TEXT")
    private String notes;

    public Item getItem() {
        return item;
    }

    public void setItem(Item item) {
        this.item = item;
    }

    public ServiceType getServiceType() {
        return serviceType;
    }

    public void setServiceType(ServiceType serviceType) {
        this.serviceType = serviceType;
    }

    public Vendor getPreferredVendor() {
        return preferredVendor;
    }

    public void setPreferredVendor(Vendor preferredVendor) {
        this.preferredVendor = preferredVendor;
    }

    public FrequencyUnit getFrequencyUnit() {
        return frequencyUnit;
    }

    public void setFrequencyUnit(
            FrequencyUnit frequencyUnit) {
        this.frequencyUnit = frequencyUnit;
    }

    public Integer getFrequencyInterval() {
        return frequencyInterval;
    }

    public void setFrequencyInterval(
            Integer frequencyInterval) {
        this.frequencyInterval = frequencyInterval;
    }

    public LocalDate getFirstDueDate() {
        return firstDueDate;
    }

    public void setFirstDueDate(LocalDate firstDueDate) {
        this.firstDueDate = firstDueDate;
    }

    public LocalDate getNextDueDate() {
        return nextDueDate;
    }

    public void setNextDueDate(LocalDate nextDueDate) {
        this.nextDueDate = nextDueDate;
    }

    public LocalDate getLastCompletedDate() {
        return lastCompletedDate;
    }

    public void setLastCompletedDate(
            LocalDate lastCompletedDate) {
        this.lastCompletedDate = lastCompletedDate;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public void advanceNextDueDate(
            LocalDate completedDate) {
        this.lastCompletedDate = completedDate;
        if (frequencyUnit != null
                && frequencyInterval != null) {
            this.nextDueDate = switch (frequencyUnit) {
                case days -> completedDate
                        .plusDays(frequencyInterval);
                case weeks -> completedDate
                        .plusWeeks(frequencyInterval);
                case months -> completedDate
                        .plusMonths(frequencyInterval);
                case years -> completedDate
                        .plusYears(frequencyInterval);
            };
        }
    }
}
