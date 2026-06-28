--liquibase formatted sql
--changeset shadowfrog:2
CREATE TABLE user_auths (
    id BIGSERIAL PRIMARY KEY,
    login VARCHAR(70) NOT NULL,
    password VARCHAR(70) NOT NULL,
    role VARCHAR(20) DEFAULT('STUDENT') NOT NULL
);

--changeset shadowfrog:3
ALTER TABLE students
ADD COLUMN user_auth_id BIGINT,
ADD CONSTRAINT fk_students_user_auth_id FOREIGN KEY (user_auth_id) REFERENCES user_auths(id) ON DELETE CASCADE;