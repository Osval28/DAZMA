package co.dazma.api.users.domain;

import co.dazma.api.shared.domain.errors.DomainInvalidException;

public class InvalidUserException extends DomainInvalidException {

    public InvalidUserException(UserErrorCode errorCode, Object... arguments) {
        super(errorCode, arguments);
    }
}
