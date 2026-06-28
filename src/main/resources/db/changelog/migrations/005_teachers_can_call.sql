--liquibase formatted sql
--changeset shadowfrog:fix_opechatka
ALTER TABLE teachers
RENAME COLUMN phone TO phone_number;