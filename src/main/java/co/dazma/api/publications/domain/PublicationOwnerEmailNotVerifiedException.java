package co.dazma.api.publications.domain;

import co.dazma.api.shared.domain.errors.DomainNotAllowedException;

public class PublicationOwnerEmailNotVerifiedException extends DomainNotAllowedException {

    public PublicationOwnerEmailNotVerifiedException() {
        super(PublicationErrorCode.OWNER_EMAIL_NOT_VERIFIED);
    }
}
