package co.dazma.api.chats.domain.exceptions;

import co.dazma.api.chats.domain.enums.ChatErrorCode;
import co.dazma.api.shared.domain.errors.DomainNotAllowedException;

public class SelfChatNotAllowedException extends DomainNotAllowedException {

    public SelfChatNotAllowedException() {
        super(ChatErrorCode.SELF_CHAT_NOT_ALLOWED);
    }
}