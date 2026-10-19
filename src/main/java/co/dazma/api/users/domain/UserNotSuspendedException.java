package co.dazma.api.users.domain;

import co.dazma.api.shared.domain.errors.DomainException;

public class UserNotSuspendedException extends DomainException {

    public UserNotSuspendedException() {
        super(UserErrorCode.NOT_SUSPENDED);
    }
}
