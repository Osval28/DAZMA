package co.dazma.api.chats.domain;

import co.dazma.api.shared.domain.errors.DomainConflictException;

public class ChatParticipantNotReadyException extends DomainConflictException {

    public ChatParticipantNotReadyException() {
        super(ChatErrorCode.PARTICIPANT_NOT_READY);
    }
}