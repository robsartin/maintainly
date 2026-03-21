CREATE TABLE audit_entries (
    id              UUID        NOT NULL PRIMARY KEY,
    organization_id UUID        NOT NULL REFERENCES organizations(id),
    username        VARCHAR(200) NOT NULL,
    entity_type     VARCHAR(50)  NOT NULL,
    entity_id       UUID        NOT NULL,
    entity_name     VARCHAR(250) NOT NULL,
    action          VARCHAR(20)  NOT NULL,
    details         TEXT,
    timestamp       TIMESTAMPTZ  NOT NULL DEFAULT now()
);

CREATE INDEX idx_audit_entries_org_ts
    ON audit_entries (organization_id, timestamp DESC);

CREATE INDEX idx_audit_entries_entity
    ON audit_entries (entity_id);
