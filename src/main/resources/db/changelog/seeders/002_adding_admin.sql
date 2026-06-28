--liquibase formatted sql
--changeset shadowfrog:installing_pgcrypto context:dev
CREATE EXTENSION IF NOT EXISTS pgcrypto;

--changeset shadowfrog:insert_admin_user context:dev
INSERT INTO user_auths (login, password, role)
VALUES (
    'shadowfrog',
    crypt('psinus', gen_salt('bf')),
    'ADMIN'
);