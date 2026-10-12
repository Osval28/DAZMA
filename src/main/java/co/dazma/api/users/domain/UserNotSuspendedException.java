package co.dazma.api.users.domain;

public class UserNotSuspendedException extends IllegalStateException {
    public UserNotSuspendedException() {
        super("El usuario no está suspendido");
    }
}
