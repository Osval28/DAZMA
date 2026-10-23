package co.dazma.api.security.services.usecases;

import co.dazma.api.security.services.input.dto.OpenSessionInputDto;
import co.dazma.api.security.services.input.dto.OpenSessionOutputDto;
import co.dazma.api.security.services.input.ports.OpenSessionUseCase;
import co.dazma.api.security.services.output.ports.SessionTokenIssuer;

import java.time.Duration;

public class OpenSessionService implements OpenSessionUseCase {

    private final SessionTokenIssuer sessionTokenIssuer;
    private final Duration sessionValidity;

    public OpenSessionService(SessionTokenIssuer sessionTokenIssuer, Duration sessionValidity) {
        this.sessionTokenIssuer = sessionTokenIssuer;
        this.sessionValidity = sessionValidity;
    }

    @Override
    public OpenSessionOutputDto open(OpenSessionInputDto input) {
        String sessionToken = sessionTokenIssuer.issue(input.idToken(), sessionValidity);
        return new OpenSessionOutputDto(sessionToken, sessionValidity);
    }
}
