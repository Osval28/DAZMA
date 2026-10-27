package co.dazma.api.chats.domain;

import co.dazma.api.shared.domain.errors.DomainInvalidException;

public class InvalidChatParticipantException extends DomainInvalidException {

    public InvalidChatParticipantException(ChatErrorCode errorCode) {
        super(errorCode);
    }
}