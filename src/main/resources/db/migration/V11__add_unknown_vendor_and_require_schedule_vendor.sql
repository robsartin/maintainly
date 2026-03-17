-- Add system_managed flag to vendors and require vendor on schedules.

ALTER TABLE vendors
    ADD COLUMN system_managed BOOLEAN NOT NULL DEFAULT false;

-- Create an "Unknown Vendor" for every organization that has schedules
-- with a NULL preferred_vendor_id.
INSERT INTO vendors (id, organization_id, name, system_managed, created_at, updated_at)
SELECT gen_random_uuid(), s.organization_id, 'Unknown Vendor', true, NOW(), NOW()
FROM service_schedules s
WHERE s.preferred_vendor_id IS NULL
GROUP BY s.organization_id
ON CONFLICT DO NOTHING;

-- Also create "Unknown Vendor" for orgs that don't have one yet
-- but do have at least one schedule (covers orgs where all schedules
-- already have vendors).
INSERT INTO vendors (id, organization_id, name, system_managed, created_at, updated_at)
SELECT gen_random_uuid(), o.id, 'Unknown Vendor', true, NOW(), NOW()
FROM organizations o
WHERE NOT EXISTS (
    SELECT 1 FROM vendors v
    WHERE v.organization_id = o.id AND v.system_managed = true
)
AND EXISTS (
    SELECT 1 FROM service_schedules ss
    WHERE ss.organization_id = o.id
);

-- Point orphaned schedules at their org's Unknown Vendor.
UPDATE service_schedules s
SET preferred_vendor_id = (
    SELECT v.id FROM vendors v
    WHERE v.organization_id = s.organization_id
      AND v.system_managed = true
    LIMIT 1
)
WHERE s.preferred_vendor_id IS NULL;

-- Now make preferred_vendor_id NOT NULL.
ALTER TABLE service_schedules
    ALTER COLUMN preferred_vendor_id SET NOT NULL;
