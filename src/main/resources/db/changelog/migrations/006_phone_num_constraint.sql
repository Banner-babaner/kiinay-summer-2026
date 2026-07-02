--liquibase formatted sql

--changeset shadowfrog:clean_invalid_phones
UPDATE students
SET phone_number = NULL
WHERE phone_number IS NOT NULL
  AND phone_number !~ '^\+?[0-9]{10,15}$';


--changeset shadowfrog:alter_phone_size
ALTER TABLE students
ALTER COLUMN phone_number TYPE VARCHAR(15);

--changeset shadowfrog:add_phone_validation
ALTER TABLE students
ADD CONSTRAINT chk_students_phone_number
CHECK (phone_number IS NULL OR phone_number ~ '^\+?[0-9]{10,15}$');