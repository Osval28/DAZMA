package co.dazma.api.chats.domain.exceptions;

import co.dazma.api.chats.domain.enums.ChatErrorCode;
import co.dazma.api.shared.domain.errors.DomainNotAllowedException;

public class ChatEmailNotVerifiedException extends DomainNotAllowedException {

    public ChatEmailNotVerifiedException() {
        super(ChatErrorCode.EMAIL_NOT_VERIFIED);
    }
}
