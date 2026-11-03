package co.dazma.api.users.domain.exceptions;

import co.dazma.api.shared.domain.errors.DomainConflictException;
import co.dazma.api.users.domain.enums.UserErrorCode;

public class UserAlreadySuspendedException extends DomainConflictException {

    public UserAlreadySuspendedException() {
        super(UserErrorCode.ALREADY_SUSPENDED);
    }
}
