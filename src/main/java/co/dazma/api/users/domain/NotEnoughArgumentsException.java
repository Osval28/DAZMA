package co.dazma.api.users.domain;

public class NotEnoughArgumentsException extends RuntimeException {
    public NotEnoughArgumentsException(String field) {
        super("No se proporcionó el argumento requerido: " + field);
    }
}
