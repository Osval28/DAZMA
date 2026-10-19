package co.dazma.api.publications.domain;

import co.dazma.api.shared.domain.errors.DomainException;

import java.util.UUID;

public class PublicationNotFoundException extends DomainException {

    public PublicationNotFoundException(UUID publicationId) {
        super(PublicationErrorCode.NOT_FOUND, publicationId);
    }
}
