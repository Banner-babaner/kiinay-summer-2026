--liquibase formatted sql
--changeset shadowfrog:create_admins_table
CREATE TABLE admins (
    id BIGSERIAL PRIMARY KEY,
    fio VARCHAR(64) NOT NULL,
    phone_number VARCHAR(24),
    user_auth_id BIGINT,
    CONSTRAINT fk_admins_user_auth_id FOREIGN KEY (user_auth_id) REFERENCES user_auths(id)
);
