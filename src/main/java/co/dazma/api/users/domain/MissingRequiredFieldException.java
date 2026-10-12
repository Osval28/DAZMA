package co.dazma.api.users.domain;

public class MissingRequiredFieldException extends IllegalArgumentException {
    public MissingRequiredFieldException(String field) {
        super("Falta el campo: " + field);
    }
}
