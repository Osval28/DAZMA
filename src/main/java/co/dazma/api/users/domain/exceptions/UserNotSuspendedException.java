package co.dazma.api.users.domain.exceptions;

import co.dazma.api.shared.domain.errors.DomainConflictException;
import co.dazma.api.users.domain.enums.UserErrorCode;

public class UserNotSuspendedException extends DomainConflictException {

    public UserNotSuspendedException() {
        super(UserErrorCode.NOT_SUSPENDED);
    }
}
