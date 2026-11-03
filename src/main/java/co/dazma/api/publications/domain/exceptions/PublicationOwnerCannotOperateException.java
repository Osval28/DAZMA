package co.dazma.api.publications.domain.exceptions;

import co.dazma.api.publications.domain.enums.PublicationErrorCode;
import co.dazma.api.shared.domain.errors.DomainNotAllowedException;

public class PublicationOwnerCannotOperateException extends DomainNotAllowedException {

    public PublicationOwnerCannotOperateException() {
        super(PublicationErrorCode.OWNER_CANNOT_OPERATE);
    }
}
