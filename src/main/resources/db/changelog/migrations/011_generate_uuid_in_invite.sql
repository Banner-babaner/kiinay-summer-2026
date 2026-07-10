--liquibase formatted sql

--changeset shadowfrog:change_invite_secret_key_type
ALTER TABLE invite_applications ALTER COLUMN secret_key TYPE UUID
USING secret_key::UUID;

--changeset shadowfrog:change_invite_secret_key_default
ALTER TABLE invite_applications ALTER COLUMN secret_key SET DEFAULT gen_random_uuid()

--changeset shadowfrog:change_invite_secret_key_mot_null
ALTER TABLE invite_applications
ADD CONSTRAINT uk_invite_secret_key UNIQUE (secret_key);