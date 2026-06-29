--liquibase formatted sql

--changeset shadowfrog:create_admin_for_shadowfrog context:dev
INSERT INTO admins(fio, phone_number, user_auth_id)
VALUES ('ShadowFrog', '89053243304', (SELECT id FROM user_auths ua WHERE ua.login='shadowfrog'))