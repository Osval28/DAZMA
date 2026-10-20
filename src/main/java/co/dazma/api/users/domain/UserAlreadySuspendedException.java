package co.dazma.api.users.domain;

import co.dazma.api.shared.domain.errors.DomainException;

public class UserAlreadySuspendedException extends DomainException {

    public UserAlreadySuspendedException() {
        super(UserErrorCode.ALREADY_SUSPENDED);
    }
}
