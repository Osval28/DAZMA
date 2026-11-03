package co.dazma.api.users.domain.exceptions;

import co.dazma.api.shared.domain.errors.DomainNotFoundException;
import co.dazma.api.users.domain.enums.UserErrorCode;

public class UserNotFoundException extends DomainNotFoundException {

    public UserNotFoundException() {
        super(UserErrorCode.NOT_FOUND);
    }

}
