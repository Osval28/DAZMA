package co.dazma.api.chats.domain;

import co.dazma.api.shared.domain.errors.DomainInvalidException;

public class InvalidChatRoomException extends DomainInvalidException {

    public InvalidChatRoomException(ChatErrorCode errorCode) {
        super(errorCode);
    }
}