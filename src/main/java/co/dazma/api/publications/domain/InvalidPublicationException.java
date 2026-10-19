package co.dazma.api.publications.domain;

import co.dazma.api.shared.domain.errors.DomainException;

public class InvalidPublicationException extends DomainException {

    public InvalidPublicationException(PublicationErrorCode errorCode, Object... arguments) {
        super(errorCode, arguments);
    }
}
