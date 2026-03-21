CREATE TABLE facilities (
    id              UUID PRIMARY KEY,
    organization_id UUID NOT NULL REFERENCES organizations(id),
    name            VARCHAR(200) NOT NULL,
    address_line_1  VARCHAR(200),
    address_line_2  VARCHAR(200),
    city            VARCHAR(100),
    state_province  VARCHAR(100),
    postal_code     VARCHAR(30),
    country         VARCHAR(100),
    active          BOOLEAN NOT NULL DEFAULT TRUE,
    created_at      TIMESTAMP WITH TIME ZONE NOT NULL,
    updated_at      TIMESTAMP WITH TIME ZONE NOT NULL
);

CREATE INDEX idx_facilities_org
    ON facilities(organization_id);

ALTER TABLE items
    ADD COLUMN facility_id UUID
    REFERENCES facilities(id) ON DELETE SET NULL;
