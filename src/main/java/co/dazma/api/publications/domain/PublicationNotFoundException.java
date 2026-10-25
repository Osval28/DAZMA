package co.dazma.api.publications.domain;

import co.dazma.api.shared.domain.errors.DomainNotFoundException;

import java.util.UUID;

public class PublicationNotFoundException extends DomainNotFoundException {

    public PublicationNotFoundException(UUID publicationId) {
        super(PublicationErrorCode.NOT_FOUND, publicationId);
    }
}
