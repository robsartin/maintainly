CREATE TABLE facilities (
    id UUID PRIMARY KEY,
    organization_id UUID NOT NULL REFERENCES organizations(id),
    name VARCHAR(200) NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_facilities_org ON facilities(organization_id);

ALTER TABLE items
    ADD COLUMN facility_id UUID REFERENCES facilities(id)
        ON DELETE SET NULL;

CREATE INDEX idx_items_facility ON items(facility_id);
