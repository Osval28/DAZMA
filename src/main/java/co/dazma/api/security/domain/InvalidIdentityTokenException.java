package co.dazma.api.security.domain;

import co.dazma.api.shared.domain.errors.DomainException;

public class InvalidIdentityTokenException extends DomainException {

    public InvalidIdentityTokenException() {
        super(SecurityErrorCode.IDENTITY_TOKEN_INVALID);
    }

    public InvalidIdentityTokenException(Throwable cause) {
        super(SecurityErrorCode.IDENTITY_TOKEN_INVALID, cause);
    }
}
