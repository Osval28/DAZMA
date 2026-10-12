CREATE SCHEMA IF NOT EXISTS app_users;

CREATE TABLE app_users.users (
id            UUID PRIMARY KEY,
name          VARCHAR(255) NOT NULL,
phone         VARCHAR(255) NOT NULL,
municipality  VARCHAR(255) NOT NULL,
external_id   VARCHAR(255) NOT NULL UNIQUE,
role          VARCHAR(20)  NOT NULL,
state         VARCHAR(20)  NOT NULL
)