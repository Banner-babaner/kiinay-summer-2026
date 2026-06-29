--liquibase formatted sql
--changeset shadowfrog:create_teachers_table
CREATE TABLE teachers (
    id BIGSERIAL PRIMARY KEY,
    fio VARCHAR(64) NOT NULL,
    phone_number VARCHAR(24),
    user_auth_id BIGINT,
    CONSTRAINT fk_teachers_user_auth_id FOREIGN KEY (user_auth_id) REFERENCES user_auths(id)
);

--changeset shadowfrog:create_teacher_groups_table
CREATE TABLE teacher_groups (
    teacher_id BIGINT NOT NULL,
    group_id BIGINT NOT NULL,
    PRIMARY KEY (teacher_id, group_id),
    CONSTRAINT fk_teacher_groups_teacher
        FOREIGN KEY (teacher_id) REFERENCES teachers(id) ON DELETE CASCADE,
    CONSTRAINT fk_teacher_groups_group
        FOREIGN KEY (group_id) REFERENCES groups(id) ON DELETE CASCADE
);