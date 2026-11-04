package co.dazma.api.users.domain.exceptions;

import co.dazma.api.shared.domain.errors.DomainNotAllowedException;
import co.dazma.api.users.domain.enums.UserErrorCode;

public class UserCannotOperateExceptionDomain extends DomainNotAllowedException {

    public UserCannotOperateExceptionDomain() {
        super(UserErrorCode.CANNOT_OPERATE);
    }
}
