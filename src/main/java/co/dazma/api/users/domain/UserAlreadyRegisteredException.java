package co.dazma.api.users.domain;

public class UserAlreadyRegisteredException extends IllegalStateException {

    public UserAlreadyRegisteredException() {
        super("El usuario ya tiene un perfil registrado");
    }
}
