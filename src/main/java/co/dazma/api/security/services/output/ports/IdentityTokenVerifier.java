package co.dazma.api.security.services.output.ports;

public interface IdentityTokenVerifier {

    String verify(String token);
}
