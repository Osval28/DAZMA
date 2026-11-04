package co.dazma.api.chats.domain.exceptions;

import co.dazma.api.chats.domain.enums.ChatErrorCode;
import co.dazma.api.shared.domain.errors.DomainNotFoundException;

import java.util.UUID;

public class ChatOfferNotFoundException extends DomainNotFoundException {

    public ChatOfferNotFoundException(UUID publicationId) {
        super(ChatErrorCode.OFFER_NOT_FOUND, publicationId);
    }
}
