package co.dazma.api.security.domain.exceptions;

import co.dazma.api.security.domain.enums.SecurityErrorCode;
import co.dazma.api.shared.domain.errors.DomainException;

public class SessionRevocationException extends DomainException {

    public SessionRevocationException(Throwable cause) {
        super(SecurityErrorCode.SESSION_REVOCATION_FAILED, cause);
    }
}
