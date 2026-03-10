CREATE INDEX idx_items_org_name
    ON items (organization_id, name);

CREATE INDEX idx_items_org_location
    ON items (organization_id, LOWER(location));

CREATE INDEX idx_schedules_org_active_due
    ON service_schedules (organization_id, active, next_due_date);
