package co.dazma.api.users.domain;

import co.dazma.api.shared.domain.errors.DomainConflictException;

public class UserAlreadyRegisteredException extends DomainConflictException {

    public UserAlreadyRegisteredException() {
        super(UserErrorCode.ALREADY_REGISTERED);
    }
}
