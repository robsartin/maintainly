DELETE FROM service_requests;
DELETE FROM properties;
DELETE FROM app_users;
DELETE FROM organizations;

ALTER TABLE properties DROP CONSTRAINT properties_organization_id_fkey;
ALTER TABLE app_users DROP CONSTRAINT app_users_organization_id_fkey;

DROP INDEX idx_properties_org_service;

ALTER TABLE organizations DROP CONSTRAINT organizations_pkey;
ALTER TABLE organizations DROP COLUMN id;
ALTER TABLE organizations ADD COLUMN id UUID PRIMARY KEY;

ALTER TABLE app_users DROP COLUMN organization_id;
ALTER TABLE app_users ADD COLUMN organization_id UUID
    REFERENCES organizations(id);

ALTER TABLE properties DROP COLUMN organization_id;
ALTER TABLE properties ADD COLUMN organization_id UUID
    NOT NULL REFERENCES organizations(id);

CREATE INDEX idx_properties_org_service
    ON properties(organization_id, next_service_date);
