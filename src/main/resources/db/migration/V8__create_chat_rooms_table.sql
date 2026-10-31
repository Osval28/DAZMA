CREATE SCHEMA IF NOT EXISTS app_chats;

CREATE TABLE app_chats.chat_rooms (
    id             UUID PRIMARY KEY,
    publication_id UUID NOT NULL,
    owner_id       UUID NOT NULL,
    interested_id  UUID NOT NULL,
    created_at     TIMESTAMP WITH TIME ZONE NOT NULL,

    CONSTRAINT chk_chat_rooms_distinct_participants
        CHECK (owner_id <> interested_id),
    CONSTRAINT uq_chat_rooms_publication_interested
        UNIQUE (publication_id, interested_id)
);

CREATE INDEX idx_chat_rooms_owner_id
    ON app_chats.chat_rooms(owner_id);

CREATE INDEX idx_chat_rooms_interested_id
    ON app_chats.chat_rooms(interested_id);