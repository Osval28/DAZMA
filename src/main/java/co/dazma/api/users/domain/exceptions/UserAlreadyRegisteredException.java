package co.dazma.api.users.domain.exceptions;

import co.dazma.api.shared.domain.errors.DomainConflictException;
import co.dazma.api.users.domain.enums.UserErrorCode;

public class UserAlreadyRegisteredException extends DomainConflictException {

    public UserAlreadyRegisteredException() {
        super(UserErrorCode.ALREADY_REGISTERED);
    }
}
