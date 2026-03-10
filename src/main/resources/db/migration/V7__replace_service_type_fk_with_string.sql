-- Replace service_type_id FK with a plain string column
-- Migrate existing data by copying the service type name

ALTER TABLE service_schedules
    ADD COLUMN service_type VARCHAR(150);

UPDATE service_schedules ss
    SET service_type = st.name
    FROM service_types st
    WHERE ss.service_type_id = st.id;

ALTER TABLE service_schedules
    ALTER COLUMN service_type SET NOT NULL;

ALTER TABLE service_schedules
    DROP COLUMN service_type_id;

ALTER TABLE service_records
    ADD COLUMN service_type VARCHAR(150);

UPDATE service_records sr
    SET service_type = st.name
    FROM service_types st
    WHERE sr.service_type_id = st.id;

ALTER TABLE service_records
    DROP COLUMN service_type_id;

DROP INDEX IF EXISTS idx_service_types_org;
DROP INDEX IF EXISTS idx_service_types_code;
DROP INDEX IF EXISTS idx_schedules_service_type;
DROP INDEX IF EXISTS idx_records_service_type;

DROP TABLE service_types;

CREATE INDEX idx_schedules_service_type
    ON service_schedules(service_type);
CREATE INDEX idx_records_service_type
    ON service_records(service_type);
