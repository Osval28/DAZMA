package co.dazma.api.chats.domain.exceptions;

import co.dazma.api.chats.domain.enums.ChatErrorCode;
import co.dazma.api.shared.domain.errors.DomainConflictException;

public class ChatParticipantNotReadyException extends DomainConflictException {

    public ChatParticipantNotReadyException() {
        super(ChatErrorCode.PARTICIPANT_NOT_READY);
    }
}