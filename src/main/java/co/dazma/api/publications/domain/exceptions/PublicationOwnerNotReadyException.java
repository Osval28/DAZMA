package co.dazma.api.publications.domain.exceptions;

import co.dazma.api.publications.domain.enums.PublicationErrorCode;
import co.dazma.api.shared.domain.errors.DomainConflictException;

public class PublicationOwnerNotReadyException extends DomainConflictException {

    public PublicationOwnerNotReadyException() {
        super(PublicationErrorCode.OWNER_NOT_READY);
    }
}
