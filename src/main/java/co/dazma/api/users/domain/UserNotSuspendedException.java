package co.dazma.api.users.domain;

import co.dazma.api.shared.domain.errors.DomainConflictException;

public class UserNotSuspendedException extends DomainConflictException {

    public UserNotSuspendedException() {
        super(UserErrorCode.NOT_SUSPENDED);
    }
}
