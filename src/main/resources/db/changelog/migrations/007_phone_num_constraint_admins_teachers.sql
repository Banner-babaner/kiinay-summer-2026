--liquibase formatted sql

--changeset shadowfrog:clean_invalid_phones_teachers
UPDATE teachers
SET phone_number = NULL
WHERE phone_number IS NOT NULL
  AND phone_number !~ '^\+?[0-9]{10,15}$';

  --changeset shadowfrog:clean_invalid_phones_admins
  UPDATE admins
  SET phone_number = NULL
  WHERE phone_number IS NOT NULL
    AND phone_number !~ '^\+?[0-9]{10,15}$';


--changeset shadowfrog:alter_phone_size_teachers
ALTER TABLE teachers
ALTER COLUMN phone_number TYPE VARCHAR(15);

--changeset shadowfrog:alter_phone_size_admin
ALTER TABLE admins
ALTER COLUMN phone_number TYPE VARCHAR(15);


--changeset shadowfrog:add_phone_validation_teachers
ALTER TABLE teachers
ADD CONSTRAINT chk_teachers_phone_number
CHECK (phone_number IS NULL OR phone_number ~ '^\+?[0-9]{10,15}$');

--changeset shadowfrog:add_phone_validation_admins
ALTER TABLE admins
ADD CONSTRAINT chk_admins_phone_number
CHECK (phone_number IS NULL OR phone_number ~ '^\+?[0-9]{10,15}$');