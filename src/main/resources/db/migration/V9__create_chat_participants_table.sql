CREATE TABLE app_chats.chat_participants (
    user_id     UUID PRIMARY KEY,
    external_id VARCHAR(255) NOT NULL UNIQUE,
    state       VARCHAR(20)  NOT NULL,

    CONSTRAINT chk_chat_participants_state
        CHECK (state IN ('ACTIVE', 'SUSPENDED'))
);