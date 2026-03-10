@ database schema
CREATE TABLE vendors (
    id UUID PRIMARY KEY,
    name VARCHAR(200) NOT NULL,
    phone VARCHAR(50),
    email VARCHAR(320),
    address_line_1 VARCHAR(200),
    address_line_2 VARCHAR(200),
    city VARCHAR(100),
    state_province VARCHAR(100),
    postal_code VARCHAR(30),
    country VARCHAR(100),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE vendor_alt_phones (
    id UUID PRIMARY KEY,
    vendor_id UUID NOT NULL REFERENCES vendors(id) ON DELETE CASCADE,
    phone VARCHAR(50) NOT NULL,
    label VARCHAR(50),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE service_types (
    id UUID PRIMARY KEY,
    code VARCHAR(100) NOT NULL UNIQUE,
    name VARCHAR(150) NOT NULL,
    description TEXT,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE items (
    id UUID PRIMARY KEY,
    name VARCHAR(200) NOT NULL,
    location VARCHAR(200),
    manufacturer VARCHAR(200),
    model_name VARCHAR(200),
    model_number VARCHAR(200),
    model_year INTEGER,
    serial_number VARCHAR(200),
    purchase_date DATE,
    notes TEXT,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT chk_items_model_year
        CHECK (model_year IS NULL OR model_year BETWEEN 1900 AND 3000)
);

CREATE TABLE service_schedules (
    id UUID PRIMARY KEY,
    item_id UUID NOT NULL REFERENCES items(id) ON DELETE CASCADE,
    service_type_id UUID NOT NULL REFERENCES service_types(id),
    preferred_vendor_id UUID REFERENCES vendors(id) ON DELETE SET NULL,

    frequency_unit VARCHAR(20) NOT NULL,
    frequency_interval INTEGER NOT NULL,

    first_due_date DATE,
    next_due_date DATE,
    last_completed_date DATE,

    active BOOLEAN NOT NULL DEFAULT TRUE,
    notes TEXT,

    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT chk_service_schedules_frequency_unit
        CHECK (frequency_unit IN ('days', 'weeks', 'months', 'years')),
    CONSTRAINT chk_service_schedules_frequency_interval
        CHECK (frequency_interval > 0)
);

CREATE TABLE service_records (
    id UUID PRIMARY KEY,
    item_id UUID NOT NULL REFERENCES items(id) ON DELETE CASCADE,
    service_type_id UUID REFERENCES service_types(id),
    service_schedule_id UUID REFERENCES service_schedules(id) ON DELETE SET NULL,
    vendor_id UUID REFERENCES vendors(id) ON DELETE SET NULL,

    data_entry_timestamp TIMESTAMP NOT NULL,
    service_date DATE NOT NULL,

    summary VARCHAR(250),
    description TEXT,
    cost NUMERIC(12,2),

    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

@@ indexes
CREATE INDEX idx_items_name ON items(name);
CREATE INDEX idx_items_location ON items(location);

CREATE INDEX idx_vendors_name ON vendors(name);

CREATE INDEX idx_vendor_alt_phones_vendor_id ON vendor_alt_phones(vendor_id);

CREATE INDEX idx_service_types_code ON service_types(code);

CREATE INDEX idx_service_schedules_item_id ON service_schedules(item_id);
CREATE INDEX idx_service_schedules_service_type_id ON service_schedules(service_type_id);
CREATE INDEX idx_service_schedules_preferred_vendor_id ON service_schedules(preferred_vendor_id);
CREATE INDEX idx_service_schedules_next_due_date ON service_schedules(next_due_date);
CREATE INDEX idx_service_schedules_active ON service_schedules(active);

CREATE INDEX idx_service_records_item_id ON service_records(item_id);
CREATE INDEX idx_service_records_vendor_id ON service_records(vendor_id);
CREATE INDEX idx_service_records_service_type_id ON service_records(service_type_id);
CREATE INDEX idx_service_records_service_schedule_id ON service_records(service_schedule_id);
CREATE INDEX idx_service_records_service_date ON service_records(service_date);


# code

@@ BaseEntity

package com.example.assets.model;

import jakarta.persistence.Column;
import jakarta.persistence.Id;
import jakarta.persistence.MappedSuperclass;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;

import java.time.Instant;
import java.util.UUID;

@MappedSuperclass
public abstract class BaseEntity {

    @Id
    @Column(nullable = false, updatable = false)
    private UUID id;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    @PrePersist
    protected void onCreate() {
        Instant now = Instant.now();
        this.createdAt = now;
        this.updatedAt = now;
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = Instant.now();
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }
}

@@ Vendor 

package com.example.assets.model;

import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "vendors")
public class Vendor extends BaseEntity {

    @Column(nullable = false, length = 200)
    private String name;

    @Column(length = 50)
    private String phone;

    @Column(length = 320)
    private String email;

    @Column(name = "address_line_1", length = 200)
    private String addressLine1;

    @Column(name = "address_line_2", length = 200)
    private String addressLine2;

    @Column(length = 100)
    private String city;

    @Column(name = "state_province", length = 100)
    private String stateProvince;

    @Column(name = "postal_code", length = 30)
    private String postalCode;

    @Column(length = 100)
    private String country;

    @OneToMany(mappedBy = "vendor", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<VendorAltPhone> altPhones = new ArrayList<>();

    // getters/setters
}

@@ VendorAltPhone

package com.example.assets.model;

import jakarta.persistence.*;

@Entity
@Table(name = "vendor_alt_phones")
public class VendorAltPhone extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "vendor_id", nullable = false)
    private Vendor vendor;

    @Column(nullable = false, length = 50)
    private String phone;

    @Column(length = 50)
    private String label;

    // getters/setters
}

@@ ServiceType

package com.example.assets.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

@Entity
@Table(name = "service_types")
public class ServiceType extends BaseEntity {

    @Column(nullable = false, unique = true, length = 100)
    private String code;

    @Column(nullable = false, length = 150)
    private String name;

    @Column(columnDefinition = "TEXT")
    private String description;

    // getters/setters
}

@@ Item

package com.example.assets.model;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "items")
public class Item extends BaseEntity {

    @Column(nullable = false, length = 200)
    private String name;

    @Column(length = 200)
    private String location;

    @Column(length = 200)
    private String manufacturer;

    @Column(name = "model_name", length = 200)
    private String modelName;

    @Column(name = "model_number", length = 200)
    private String modelNumber;

    @Column(name = "model_year")
    private Integer modelYear;

    @Column(name = "serial_number", length = 200)
    private String serialNumber;

    @Column(name = "purchase_date")
    private LocalDate purchaseDate;

    @Column(columnDefinition = "TEXT")
    private String notes;

    @OneToMany(mappedBy = "item", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ServiceSchedule> serviceSchedules = new ArrayList<>();

    @OneToMany(mappedBy = "item", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ServiceRecord> serviceRecords = new ArrayList<>();

    // getters/setters
}

@@ FrequemcyUnit 

package com.example.assets.model;

public enum FrequencyUnit {
    days,
    weeks,
    months,
    years
}

@@ ServiceSchedule

package com.example.assets.model;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "service_schedules")
public class ServiceSchedule extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "item_id", nullable = false)
    private Item item;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "service_type_id", nullable = false)
    private ServiceType serviceType;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "preferred_vendor_id")
    private Vendor preferredVendor;

    @Enumerated(EnumType.STRING)
    @Column(name = "frequency_unit", nullable = false, length = 20)
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

    // getters/setters
}

@@ ServiceRecord

package com.example.assets.model;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;

@Entity
@Table(name = "service_records")
public class ServiceRecord extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "item_id", nullable = false)
    private Item item;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "service_type_id")
    private ServiceType serviceType;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "service_schedule_id")
    private ServiceSchedule serviceSchedule;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "vendor_id")
    private Vendor vendor;

    @Column(name = "data_entry_timestamp", nullable = false)
    private Instant dataEntryTimestamp;

    @Column(name = "service_date", nullable = false)
    private LocalDate serviceDate;

    @Column(length = 250)
    private String summary;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(precision = 12, scale = 2)
    private BigDecimal cost;

    @PrePersist
    protected void onPersist() {
        if (dataEntryTimestamp == null) {
            dataEntryTimestamp = Instant.now();
        }
    }

    // getters/setters
}

A couple of implementation points matter here:

If you use PostgreSQL, keep the DB columns as native uuid. Hibernate supports UUID mapping, and PostgreSQL supports the uuid type directly.

Because IDs are application-assigned, make sure every new entity has its ID set before persist.

I would also add a unique business rule later if needed, for example:

items(serial_number) unique when present

or (item_id, service_type_id) unique on active schedules if you want only one schedule per service type.

@ My recommendation

This is the version I’d start with in a real Spring/Hibernate app:

- UUIDv7 assigned in Java
- native PostgreSQL uuid
- normalized service_types
- separate service_schedules and service_records
- JPA entities exactly as above

The next useful step is to add:

- a Spring Data repository layer,
- - a service method to mark service complete and roll next_due_date, and
- Flyway migrations for this schema.
