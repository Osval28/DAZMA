package co.dazma.api.publications.domain;

import co.dazma.api.shared.domain.errors.DomainInvalidException;

public class InvalidPublicationException extends DomainInvalidException {

    public InvalidPublicationException(PublicationErrorCode errorCode, Object... arguments) {
        super(errorCode, arguments);
    }
}
