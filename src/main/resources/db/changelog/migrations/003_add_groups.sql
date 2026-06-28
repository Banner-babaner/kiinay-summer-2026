--liquibase formatted sql


--changeset shadowfrog:create_groups_table
CREATE TABLE groups (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(64) NOT NULL UNIQUE
);


--changeset shadowfrog:migrate_groups_data
INSERT INTO groups (name)
SELECT DISTINCT group_of_students
FROM students
WHERE group_of_students IS NOT NULL;

--changeset shadowfrog:add_group_id_to_students
ALTER TABLE students
ADD COLUMN group_id BIGINT;

--changeset shadowfrog:update_students_group_id
UPDATE students s
SET group_id = g.id
FROM groups g
WHERE s.group_of_students = g.name;

--changeset shadowfrog:add_foreign_key
ALTER TABLE students
ADD CONSTRAINT fk_students_group
FOREIGN KEY (group_id) REFERENCES groups(id) ON DELETE RESTRICT;

--changeset shadowfrog:drop_group_of_students
ALTER TABLE students
DROP COLUMN group_of_students;