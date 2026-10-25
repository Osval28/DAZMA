package co.dazma.api.users.domain;

import co.dazma.api.shared.domain.errors.DomainConflictException;
import co.dazma.api.shared.domain.errors.DomainException;

public class UserAlreadySuspendedException extends DomainConflictException {

    public UserAlreadySuspendedException() {
        super(UserErrorCode.ALREADY_SUSPENDED);
    }
}
