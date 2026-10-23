package co.dazma.api.users.domain;


public class MissingRequiredFieldException extends InvalidUserException {

    public MissingRequiredFieldException(String field) {
        super(UserErrorCode.REQUIRED_FIELD_MISSING, field);
    }
}
