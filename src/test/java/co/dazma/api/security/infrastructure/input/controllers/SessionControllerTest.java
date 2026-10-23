package co.dazma.api.security.infrastructure.input.controllers;

import co.dazma.api.security.services.input.dto.CloseSessionInputDto;
import co.dazma.api.security.services.input.dto.OpenSessionInputDto;
import co.dazma.api.security.services.input.dto.OpenSessionOutputDto;
import co.dazma.api.security.services.input.ports.CloseSessionUseCase;
import co.dazma.api.security.services.input.ports.OpenSessionUseCase;
import jakarta.servlet.http.Cookie;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.security.web.method.annotation.CsrfTokenArgumentResolver;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.Duration;

import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.containsString;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class SessionControllerTest {

    private OpenSessionUseCase openSessionUseCase;
    private CloseSessionUseCase closeSessionUseCase;
    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        openSessionUseCase = mock(OpenSessionUseCase.class);
        closeSessionUseCase = mock(CloseSessionUseCase.class);
        mockMvc = MockMvcBuilders
                .standaloneSetup(new SessionController(openSessionUseCase, closeSessionUseCase))
                .setCustomArgumentResolvers(new CsrfTokenArgumentResolver())
                .build();
    }

    @Test
    void shouldOpenASessionWithAnHttpOnlyCookie() throws Exception {
        when(openSessionUseCase.open(new OpenSessionInputDto("id-token")))
                .thenReturn(new OpenSessionOutputDto("session-token", Duration.ofHours(8)));

        mockMvc.perform(post("/api/sessions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                { "idToken": "id-token" }
                                """))
                .andExpect(status().isNoContent())
                .andExpect(header().string(HttpHeaders.SET_COOKIE, allOf(
                        containsString("dazma_session=session-token"),
                        containsString("Max-Age=28800"),
                        containsString("Path=/"),
                        containsString("Secure"),
                        containsString("HttpOnly"),
                        containsString("SameSite=Strict")
                )));
    }

    @Test
    void shouldIssueTheCsrfTokenBeforeOpeningASession() throws Exception {
        CsrfToken csrfToken = mock(CsrfToken.class);

        mockMvc.perform(get("/api/sessions/csrf")
                        .requestAttr(CsrfToken.class.getName(), csrfToken))
                .andExpect(status().isNoContent());

        verify(csrfToken).getToken();
    }

    @Test
    void shouldRejectABlankIdTokenBeforeCallingTheUseCase() throws Exception {
        mockMvc.perform(post("/api/sessions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                { "idToken": " " }
                                """))
                .andExpect(status().isBadRequest());

        verify(openSessionUseCase, never()).open(any());
    }

    @Test
    void shouldCloseTheSessionAndExpireTheCookie() throws Exception {
        mockMvc.perform(delete("/api/sessions")
                        .cookie(new Cookie("dazma_session", "session-token")))
                .andExpect(status().isNoContent())
                .andExpect(header().string(HttpHeaders.SET_COOKIE, allOf(
                        containsString("dazma_session="),
                        containsString("Max-Age=0")
                )));

        verify(closeSessionUseCase).close(new CloseSessionInputDto("session-token"));
    }

    @Test
    void shouldExpireTheCookieEvenWithoutAnOpenSession() throws Exception {
        mockMvc.perform(delete("/api/sessions"))
                .andExpect(status().isNoContent())
                .andExpect(header().string(HttpHeaders.SET_COOKIE, containsString("Max-Age=0")));

        verifyNoInteractions(closeSessionUseCase);
    }

}
