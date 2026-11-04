package co.dazma.api.users.domain.exceptions;


import co.dazma.api.users.domain.enums.UserErrorCode;

public class MissingRequiredFieldException extends InvalidUserException {

    public MissingRequiredFieldException(String field) {
        super(UserErrorCode.REQUIRED_FIELD_MISSING, field);
    }
}
