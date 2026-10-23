package co.dazma.api.security.infrastructure.input.controllers;

import co.dazma.api.security.infrastructure.input.cookies.SessionCookies;
import co.dazma.api.security.infrastructure.input.dto.OpenSessionRequestDto;
import co.dazma.api.security.services.input.dto.CloseSessionInputDto;
import co.dazma.api.security.services.input.dto.OpenSessionInputDto;
import co.dazma.api.security.services.input.dto.OpenSessionOutputDto;
import co.dazma.api.security.services.input.ports.CloseSessionUseCase;
import co.dazma.api.security.services.input.ports.OpenSessionUseCase;
import jakarta.validation.Valid;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(SessionController.PATH)
@ConditionalOnProperty(name = "firebase.enabled", havingValue = "true", matchIfMissing = true)
public class SessionController {

    public static final String PATH = "/api/sessions";

    private final OpenSessionUseCase openSessionUseCase;
    private final CloseSessionUseCase closeSessionUseCase;

    public SessionController(
            OpenSessionUseCase openSessionUseCase,
            CloseSessionUseCase closeSessionUseCase
    ) {
        this.openSessionUseCase = openSessionUseCase;
        this.closeSessionUseCase = closeSessionUseCase;
    }

    @PostMapping
    public ResponseEntity<Void> open(@RequestBody @Valid OpenSessionRequestDto request, CsrfToken csrfToken) {
        OpenSessionOutputDto session = openSessionUseCase.open(new OpenSessionInputDto(request.idToken()));
        issueCsrfToken(csrfToken);

        return ResponseEntity.noContent()
                .header(HttpHeaders.SET_COOKIE,
                        SessionCookies.open(session.sessionToken(), session.validity()).toString())
                .build();
    }

    @DeleteMapping
    public ResponseEntity<Void> close(
            @CookieValue(name = SessionCookies.NAME, required = false) String sessionToken
    ) {
        if (sessionToken != null && !sessionToken.isBlank()) {
            closeSessionUseCase.close(new CloseSessionInputDto(sessionToken));
        }

        return ResponseEntity.noContent()
                .header(HttpHeaders.SET_COOKIE, SessionCookies.expired().toString())
                .build();
    }

    private static void issueCsrfToken(CsrfToken csrfToken) {
        csrfToken.getToken();
    }
}
