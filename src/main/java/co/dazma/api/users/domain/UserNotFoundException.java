package co.dazma.api.users.domain;

import co.dazma.api.shared.domain.errors.DomainNotFoundException;

public class UserNotFoundException extends DomainNotFoundException {

    public UserNotFoundException() {
        super(UserErrorCode.NOT_FOUND);
    }

}
