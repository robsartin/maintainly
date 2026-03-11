ALTER TABLE organizations
    ADD COLUMN profile_image BYTEA,
    ADD COLUMN profile_image_type VARCHAR(50);

ALTER TABLE app_users
    ADD COLUMN profile_image BYTEA,
    ADD COLUMN profile_image_type VARCHAR(50);
