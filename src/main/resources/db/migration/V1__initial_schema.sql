CREATE TABLE organizations (
    id SERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL
);

CREATE TABLE app_users (
    id UUID PRIMARY KEY,
    username VARCHAR(255) NOT NULL UNIQUE,
    organization_id INTEGER REFERENCES organizations(id)
);

CREATE TABLE properties (
    id UUID PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    address VARCHAR(500),
    next_service_date DATE,
    organization_id INTEGER NOT NULL REFERENCES organizations(id)
);

CREATE TABLE service_requests (
    id UUID PRIMARY KEY,
    property_id UUID NOT NULL REFERENCES properties(id),
    description VARCHAR(1000) NOT NULL,
    service_date DATE,
    completed BOOLEAN NOT NULL DEFAULT FALSE
);

CREATE INDEX idx_properties_org_service
    ON properties(organization_id, next_service_date);

CREATE INDEX idx_service_requests_property
    ON service_requests(property_id);
