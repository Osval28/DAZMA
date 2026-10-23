package co.dazma.api.users.domain;

import co.dazma.api.shared.domain.errors.DomainNotAllowedException;

public class UserCannotOperateExceptionDomain extends DomainNotAllowedException {

    public UserCannotOperateExceptionDomain() {
        super(UserErrorCode.CANNOT_OPERATE);
    }
}
