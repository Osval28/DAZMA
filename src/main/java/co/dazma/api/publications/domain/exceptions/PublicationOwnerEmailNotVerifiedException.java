package co.dazma.api.publications.domain.exceptions;

import co.dazma.api.publications.domain.enums.PublicationErrorCode;
import co.dazma.api.shared.domain.errors.DomainNotAllowedException;

public class PublicationOwnerEmailNotVerifiedException extends DomainNotAllowedException {

    public PublicationOwnerEmailNotVerifiedException() {
        super(PublicationErrorCode.OWNER_EMAIL_NOT_VERIFIED);
    }
}
