-- Make cost required on service_records, defaulting existing NULLs to 0.
UPDATE service_records SET cost = 0 WHERE cost IS NULL;
ALTER TABLE service_records
    ALTER COLUMN cost SET DEFAULT 0,
    ALTER COLUMN cost SET NOT NULL;
