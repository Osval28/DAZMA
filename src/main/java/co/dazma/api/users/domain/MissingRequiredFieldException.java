package co.dazma.api.users.domain;

import co.dazma.api.shared.domain.errors.DomainException;

public class MissingRequiredFieldException extends DomainException {

    public MissingRequiredFieldException(String field) {
        super(UserErrorCode.REQUIRED_FIELD_MISSING, field);
    }
}
