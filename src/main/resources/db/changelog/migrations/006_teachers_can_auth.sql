--liquibase formatted sql
--changeset shadowfrog:fix_nevnimatelnost
ALTER TABLE teachers
ADD COLUMN user_auth_id BIGINT,
ADD CONSTRAINT fk_teachers_user_auth_id FOREIGN KEY (user_auth_id) REFERENCES user_auths(id) ON DELETE CASCADE
