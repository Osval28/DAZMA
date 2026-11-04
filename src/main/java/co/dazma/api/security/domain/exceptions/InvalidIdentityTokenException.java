package co.dazma.api.security.domain.exceptions;

import co.dazma.api.security.domain.enums.SecurityErrorCode;
import co.dazma.api.shared.domain.errors.DomainUnauthenticatedException;

public class InvalidIdentityTokenException extends DomainUnauthenticatedException {

    public InvalidIdentityTokenException() {
        super(SecurityErrorCode.IDENTITY_TOKEN_INVALID);
    }

    public InvalidIdentityTokenException(Throwable cause) {
        super(SecurityErrorCode.IDENTITY_TOKEN_INVALID, cause);
    }
}
