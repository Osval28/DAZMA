package co.dazma.api.chats.infrastructure.output.externalservices.ably;

import co.dazma.api.chats.domain.enums.ChatErrorCode;
import co.dazma.api.shared.domain.errors.TechnicalException;

public class ChatTokenIssuanceException extends TechnicalException {

    public ChatTokenIssuanceException(Throwable cause) {
        super(ChatErrorCode.TOKEN_ISSUANCE_FAILED, cause);
    }
}
