package co.dazma.api.security.services.input.ports;

public interface AuthenticateIdentityUseCase {

    String authenticate(String token, boolean checkRevoked);
}
