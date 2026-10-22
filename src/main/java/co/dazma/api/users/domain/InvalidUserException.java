package co.dazma.api.users.domain;

import co.dazma.api.shared.domain.errors.DomainException;

public class InvalidUserException extends DomainException {

    public InvalidUserException(UserErrorCode errorCode, Object... arguments) {
        super(errorCode, arguments);
    }
}
