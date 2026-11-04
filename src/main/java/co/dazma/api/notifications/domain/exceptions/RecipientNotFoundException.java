package co.dazma.api.notifications.domain.exceptions;

import co.dazma.api.notifications.domain.enums.NotificationErrorCode;
import co.dazma.api.shared.domain.errors.DomainNotFoundException;

import java.util.UUID;

public class RecipientNotFoundException extends DomainNotFoundException {

    public RecipientNotFoundException(UUID userId) {
        super(NotificationErrorCode.RECIPIENT_NOT_FOUND, userId);
    }
}
