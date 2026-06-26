--liquibase formatted sql
--changeset shadowfrog:1000 context:dev
INSERT INTO students
(fio, group_of_students, phone_number)
VALUES
('Пупкин Василий Игнтаович', 'Б1-ИФСТ-21', '8-800-555-35-35'),
('Обломов Илья Ильич', 'Б1-ИФСТ-21', '8-765-432-10-98'),
('Шариков Полиграф Полиграфович', 'Б1-ИФСТ-21', '8-888-888-88-88');