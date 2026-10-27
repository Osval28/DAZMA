package co.dazma.api.chats.domain;

import co.dazma.api.shared.domain.errors.ErrorCode;

public enum ChatErrorCode implements ErrorCode {

    ROOM_ID_REQUIRED(
            "CHAT_ROOM_ID_REQUIRED",
            "chat.room.id.required"
    ),
    PUBLICATION_REQUIRED(
            "CHAT_PUBLICATION_REQUIRED",
            "chat.publication.required"
    ),
    OWNER_REQUIRED(
            "CHAT_OWNER_REQUIRED",
            "chat.owner.required"
    ),
    INTERESTED_REQUIRED(
            "CHAT_INTERESTED_REQUIRED",
            "chat.interested.required"
    ),
    CREATED_AT_REQUIRED(
            "CHAT_CREATED_AT_REQUIRED",
            "chat.created-at.required"
    ),
    SELF_CHAT_NOT_ALLOWED(
            "CHAT_SELF_NOT_ALLOWED",
            "chat.self.not-allowed"
    );

    private final String code;
    private final String messageKey;

    ChatErrorCode(String code, String messageKey) {
        this.code = code;
        this.messageKey = messageKey;
    }

    @Override
    public String code() {
        return code;
    }

    @Override
    public String messageKey() {
        return messageKey;
    }
}