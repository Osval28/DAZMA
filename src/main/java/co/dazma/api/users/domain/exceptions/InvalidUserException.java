package co.dazma.api.users.domain.exceptions;

import co.dazma.api.shared.domain.errors.DomainInvalidException;
import co.dazma.api.users.domain.enums.UserErrorCode;

public class InvalidUserException extends DomainInvalidException {

    public InvalidUserException(UserErrorCode errorCode, Object... arguments) {
        super(errorCode, arguments);
    }
}
