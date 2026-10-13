package co.dazma.api.security.domain;

public class InvalidIdentityTokenException extends RuntimeException {

    public InvalidIdentityTokenException() {
        super("El token de identidad no es valido");
    }

    public InvalidIdentityTokenException(Throwable cause) {
        super("El token de identidad no es valido", cause);
    }
}
