ALTER TABLE app_users.users
    ADD COLUMN version BIGINT NOT NULL DEFAULT 0;

ALTER TABLE app_publications.publications
    ADD COLUMN version BIGINT NOT NULL DEFAULT 0;
