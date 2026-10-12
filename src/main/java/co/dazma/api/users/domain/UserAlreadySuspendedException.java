package co.dazma.api.users.domain;

public class UserAlreadySuspendedException extends IllegalStateException {
    public UserAlreadySuspendedException() {
        super("El usuario ya está suspendido");
    }
}
