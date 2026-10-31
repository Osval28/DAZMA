package co.dazma.api.chats.domain;

import co.dazma.api.shared.domain.errors.DomainNotAllowedException;

public class ChatParticipantCannotOperateException extends DomainNotAllowedException {

    public ChatParticipantCannotOperateException() {
        super(ChatErrorCode.PARTICIPANT_CANNOT_OPERATE);
    }
}