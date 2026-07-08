--liquibase formatted sql

--changeset shadowfrog:add_email_to_teachers
ALTER TABLE teachers
ADD COLUMN email VARCHAR(255);

--changeset shadowfrog:add_constraint_email_to_teachers
ALTER TABLE teachers
ADD CONSTRAINT chk_teachers_valid_email
 CHECK(email ~ '^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\.[A-Za-z]{2,}$');


--changeset shadowfrog:add_email_to_students
ALTER TABLE students
ADD COLUMN email VARCHAR(255);

--changeset shadowfrog:add_constraint_email_to_students
ALTER TABLE students
ADD CONSTRAINT chk_students_valid_email
 CHECK(email ~ '^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\.[A-Za-z]{2,}$');

