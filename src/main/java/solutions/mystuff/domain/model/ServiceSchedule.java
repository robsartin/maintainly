package solutions.mystuff.domain.model;

import java.time.LocalDate;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

/**
 * Recurring maintenance schedule attached to an {@link Item}.
 *
 * <p>Defines a service type, recurrence frequency, and tracks the
 * next due date. Optionally references a preferred {@link Vendor}.
 *
 * <pre>{@code
 * classDiagram
 *     class ServiceSchedule {
 *         String serviceType
 *         Integer frequencyInterval
 *         FrequencyUnit frequencyUnit
 *         LocalDate nextDueDate
 *         boolean active
 *     }
 *     ServiceSchedule "*" --> "1" Item
 *     ServiceSchedule "*" --> "0..1" Vendor
 * }</pre>
 *
 * @see Item
 * @see Vendor
 * @see FrequencyUnit
 * @see OrgOwnedEntity
 */
@Entity
@Table(name = "service_schedules")
public class ServiceSchedule extends OrgOwnedEntity {

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "item_id", nullable = false)
    private Item item;

    @Column(name = "service_type", nullable = false,
            length = 150)
    private String serviceType;

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

    public String getServiceType() {
        return serviceType;
    }

    public void setServiceType(String serviceType) {
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

    /** Advance the next due date based on the completed date and frequency. */
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
