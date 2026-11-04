package co.dazma.api.publications.domain.exceptions;

import co.dazma.api.publications.domain.enums.PublicationErrorCode;
import co.dazma.api.shared.domain.errors.DomainNotAllowedException;

import java.util.UUID;

public class PublicationPhotoNotAvailableException extends DomainNotAllowedException {

    public PublicationPhotoNotAvailableException(UUID photoId) {
        super(PublicationErrorCode.PHOTO_UPLOAD_NOT_AVAILABLE, photoId);
    }
}
