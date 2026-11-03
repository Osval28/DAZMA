package co.dazma.api.publications.services.usecases.models;

import co.dazma.api.publications.domain.InvalidPublicationException;
import co.dazma.api.publications.domain.PublicationErrorCode;
import co.dazma.api.shared.domain.Municipality;

import java.util.Optional;

public record PublicationSearchCriteria(Optional<Municipality> municipality, int page, int size, int maximumSize) {

    public PublicationSearchCriteria {
        if (page < 0) {
            throw new InvalidPublicationException(PublicationErrorCode.PAGE_INVALID);
        }
        if (size < 1 || size > maximumSize) {
            throw new InvalidPublicationException(PublicationErrorCode.PAGE_SIZE_INVALID, maximumSize);
        }
    }
}
