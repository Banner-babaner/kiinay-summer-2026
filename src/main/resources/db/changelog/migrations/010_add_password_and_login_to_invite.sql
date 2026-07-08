--liquibase formatted sql

--changeset shadowfrog:add_password_and_login_to_invite
ALTER TABLE invite_applications
ADD COLUMN login VARCHAR(70) NOT NULL,
ADD COLUMN password VARCHAR(70) NOT NULL;