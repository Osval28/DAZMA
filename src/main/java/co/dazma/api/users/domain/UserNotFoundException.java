package co.dazma.api.users.domain;

import co.dazma.api.shared.domain.errors.DomainException;

public class UserNotFoundException extends DomainException {

    public UserNotFoundException() {
        super(UserErrorCode.NOT_FOUND);
    }
}
