package co.dazma.api.chats.domain.exceptions;

import co.dazma.api.chats.domain.enums.ChatErrorCode;
import co.dazma.api.shared.domain.errors.DomainNotAllowedException;

public class ChatParticipantCannotOperateException extends DomainNotAllowedException {

    public ChatParticipantCannotOperateException() {
        super(ChatErrorCode.PARTICIPANT_CANNOT_OPERATE);
    }
}