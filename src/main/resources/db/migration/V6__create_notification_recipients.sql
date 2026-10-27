CREATE SCHEMA IF NOT EXISTS app_notifications;

CREATE TABLE app_notifications.recipients (
    user_id  UUID PRIMARY KEY,
    email    VARCHAR(320) NOT NULL
);
