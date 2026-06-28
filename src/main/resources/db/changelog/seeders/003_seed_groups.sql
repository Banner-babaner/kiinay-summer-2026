--liquibase formatted sql

--changeset shadowfrog:insert_groups context:dev
INSERT INTO groups (name)
VALUES ('Б1-ИФСТ-21'), ('Б1-ИФСТ-22'), ('Б2-ИФСТ-21'), ('Б2-ИФСТ-22'), ('Б2-ИФСТ-23')
ON CONFLICT (name) DO NOTHING;