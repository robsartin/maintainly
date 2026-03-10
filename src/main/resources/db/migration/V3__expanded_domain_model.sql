DROP TABLE IF EXISTS service_requests;
DROP TABLE IF EXISTS properties;

CREATE TABLE vendors (
    id UUID PRIMARY KEY,
    organization_id UUID NOT NULL REFERENCES organizations(id),
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
    organization_id UUID NOT NULL REFERENCES organizations(id),
    vendor_id UUID NOT NULL REFERENCES vendors(id)
        ON DELETE CASCADE,
    phone VARCHAR(50) NOT NULL,
    label VARCHAR(50),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE service_types (
    id UUID PRIMARY KEY,
    organization_id UUID NOT NULL REFERENCES organizations(id),
    code VARCHAR(100) NOT NULL,
    name VARCHAR(150) NOT NULL,
    description TEXT,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT uq_service_types_org_code
        UNIQUE (organization_id, code)
);

CREATE TABLE items (
    id UUID PRIMARY KEY,
    organization_id UUID NOT NULL REFERENCES organizations(id),
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
        CHECK (model_year IS NULL
            OR model_year BETWEEN 1900 AND 3000)
);

CREATE TABLE service_schedules (
    id UUID PRIMARY KEY,
    organization_id UUID NOT NULL REFERENCES organizations(id),
    item_id UUID NOT NULL REFERENCES items(id)
        ON DELETE CASCADE,
    service_type_id UUID NOT NULL
        REFERENCES service_types(id),
    preferred_vendor_id UUID
        REFERENCES vendors(id) ON DELETE SET NULL,
    frequency_unit VARCHAR(20) NOT NULL,
    frequency_interval INTEGER NOT NULL,
    first_due_date DATE,
    next_due_date DATE,
    last_completed_date DATE,
    active BOOLEAN NOT NULL DEFAULT TRUE,
    notes TEXT,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT chk_frequency_unit
        CHECK (frequency_unit IN
            ('days', 'weeks', 'months', 'years')),
    CONSTRAINT chk_frequency_interval
        CHECK (frequency_interval > 0)
);

CREATE TABLE service_records (
    id UUID PRIMARY KEY,
    organization_id UUID NOT NULL REFERENCES organizations(id),
    item_id UUID NOT NULL REFERENCES items(id)
        ON DELETE CASCADE,
    service_type_id UUID REFERENCES service_types(id),
    service_schedule_id UUID
        REFERENCES service_schedules(id)
        ON DELETE SET NULL,
    vendor_id UUID REFERENCES vendors(id)
        ON DELETE SET NULL,
    data_entry_timestamp TIMESTAMP NOT NULL,
    service_date DATE NOT NULL,
    summary VARCHAR(250),
    description TEXT,
    cost NUMERIC(12,2),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_vendors_org ON vendors(organization_id);
CREATE INDEX idx_vendors_name ON vendors(name);

CREATE INDEX idx_vendor_alt_phones_vendor
    ON vendor_alt_phones(vendor_id);
CREATE INDEX idx_vendor_alt_phones_org
    ON vendor_alt_phones(organization_id);

CREATE INDEX idx_service_types_org
    ON service_types(organization_id);
CREATE INDEX idx_service_types_code
    ON service_types(code);

CREATE INDEX idx_items_org ON items(organization_id);
CREATE INDEX idx_items_name ON items(name);
CREATE INDEX idx_items_location ON items(location);

CREATE INDEX idx_schedules_org
    ON service_schedules(organization_id);
CREATE INDEX idx_schedules_item
    ON service_schedules(item_id);
CREATE INDEX idx_schedules_type
    ON service_schedules(service_type_id);
CREATE INDEX idx_schedules_vendor
    ON service_schedules(preferred_vendor_id);
CREATE INDEX idx_schedules_next_due
    ON service_schedules(next_due_date);
CREATE INDEX idx_schedules_active
    ON service_schedules(active);

CREATE INDEX idx_records_org
    ON service_records(organization_id);
CREATE INDEX idx_records_item
    ON service_records(item_id);
CREATE INDEX idx_records_vendor
    ON service_records(vendor_id);
CREATE INDEX idx_records_type
    ON service_records(service_type_id);
CREATE INDEX idx_records_schedule
    ON service_records(service_schedule_id);
CREATE INDEX idx_records_date
    ON service_records(service_date);
