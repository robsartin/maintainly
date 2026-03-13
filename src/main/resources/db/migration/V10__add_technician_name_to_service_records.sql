ALTER TABLE service_records
    ADD COLUMN technician_name VARCHAR(200);

-- Migrate existing technician data from description field
UPDATE service_records
SET technician_name = SUBSTRING(description FROM 14)
WHERE description LIKE 'Technician: %';
