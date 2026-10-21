package co.dazma.api.users.domain;

import co.dazma.api.shared.domain.errors.DomainException;

public class UserCannotOperateException extends DomainException {

    public UserCannotOperateException() {
        super(UserErrorCode.CANNOT_OPERATE);
    }
}
