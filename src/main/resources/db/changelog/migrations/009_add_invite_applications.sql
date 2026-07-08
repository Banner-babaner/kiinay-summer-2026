--liquibase formatted sql
--changeset shadowfrog:create_invite_applications_table
CREATE TABLE invite_applications(
id BIGSERIAL PRIMARY KEY,
fio VARCHAR(64) NOT NULL,
group_name VARCHAR(64),
phone_number VARCHAR(15),
email VARCHAR(255) NOT NULL,
created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
expires_at TIMESTAMP NOT NULL,
secret_key VARCHAR(36) NOT NULL UNIQUE,
used BOOLEAN NOT NULL DEFAULT FALSE,
role VARCHAR(20) DEFAULT('STUDENT') NOT NULL,
CONSTRAINT chk_invite_applications_phone_number
    CHECK (phone_number IS NULL OR phone_number ~ '^\+?[0-9]{10,15}$'),
CONSTRAINT chk_invite_applications_email
    CHECK(email ~ '^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\.[A-Za-z]{2,}$'),
CONSTRAINT chk_invite_applications_time CHECK(created_at < expires_at)
);