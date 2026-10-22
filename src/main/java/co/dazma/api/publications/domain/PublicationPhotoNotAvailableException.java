package co.dazma.api.publications.domain;

import co.dazma.api.shared.domain.errors.DomainException;

import java.util.UUID;

public class PublicationPhotoNotAvailableException extends DomainException {

    public PublicationPhotoNotAvailableException(UUID photoId) {
        super(PublicationErrorCode.PHOTO_UPLOAD_NOT_AVAILABLE, photoId);
    }
}
