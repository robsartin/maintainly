package solutions.mystuff.domain.model;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;

/**
 * Completed service event recorded against an {@link Item}.
 *
 * <p>Links to the originating {@link ServiceSchedule} (if any) and
 * the {@link Vendor} who performed the work. Captures a summary,
 * full description, cost, and the date the service occurred.
 *
 * <pre>{@code
 * classDiagram
 *     class ServiceRecord {
 *         String serviceType
 *         LocalDate serviceDate
 *         String summary
 *         BigDecimal cost
 *     }
 *     ServiceRecord "*" --> "1" Item
 *     ServiceRecord "*" --> "0..1" ServiceSchedule
 *     ServiceRecord "*" --> "0..1" Vendor
 * }</pre>
 *
 * @see Item
 * @see ServiceSchedule
 * @see Vendor
 * @see OrgOwnedEntity
 */
@Entity
@Table(name = "service_records")
public class ServiceRecord extends OrgOwnedEntity {

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "item_id", nullable = false)
    private Item item;

    @Column(name = "service_type", length = 150)
    private String serviceType;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "service_schedule_id")
    private ServiceSchedule serviceSchedule;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "vendor_id")
    private Vendor vendor;

    @Column(name = "data_entry_timestamp",
            nullable = false)
    private Instant dataEntryTimestamp;

    @Column(name = "service_date", nullable = false)
    private LocalDate serviceDate;

    @Column(length = 250)
    private String summary;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(precision = 12, scale = 2)
    private BigDecimal cost;

    /** Set the data-entry timestamp and delegate to the base persist callback. */
    @Override
    @PrePersist
    protected void onCreate() {
        super.onCreate();
        if (dataEntryTimestamp == null) {
            dataEntryTimestamp = Instant.now();
        }
    }

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

    public ServiceSchedule getServiceSchedule() {
        return serviceSchedule;
    }

    public void setServiceSchedule(
            ServiceSchedule serviceSchedule) {
        this.serviceSchedule = serviceSchedule;
    }

    public Vendor getVendor() {
        return vendor;
    }

    public void setVendor(Vendor vendor) {
        this.vendor = vendor;
    }

    public Instant getDataEntryTimestamp() {
        return dataEntryTimestamp;
    }

    public void setDataEntryTimestamp(
            Instant dataEntryTimestamp) {
        this.dataEntryTimestamp = dataEntryTimestamp;
    }

    public LocalDate getServiceDate() {
        return serviceDate;
    }

    public void setServiceDate(LocalDate serviceDate) {
        this.serviceDate = serviceDate;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public BigDecimal getCost() {
        return cost;
    }

    public void setCost(BigDecimal cost) {
        this.cost = cost;
    }
}
