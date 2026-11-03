package co.dazma.api.chats.domain.exceptions;

import co.dazma.api.chats.domain.enums.ChatErrorCode;
import co.dazma.api.shared.domain.errors.DomainInvalidException;

public class InvalidChatRoomException extends DomainInvalidException {

    public InvalidChatRoomException(ChatErrorCode errorCode) {
        super(errorCode);
    }
}