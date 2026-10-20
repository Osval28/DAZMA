package co.dazma.api.users.domain;

import co.dazma.api.shared.domain.errors.DomainException;

public class UserAlreadyRegisteredException extends DomainException {

    public UserAlreadyRegisteredException() {
        super(UserErrorCode.ALREADY_REGISTERED);
    }
}
