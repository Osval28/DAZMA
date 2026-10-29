package co.dazma.api.chats.domain;

import co.dazma.api.shared.domain.errors.DomainNotAllowedException;

public class SelfChatNotAllowedException extends DomainNotAllowedException {

    public SelfChatNotAllowedException() {
        super(ChatErrorCode.SELF_CHAT_NOT_ALLOWED);
    }
}