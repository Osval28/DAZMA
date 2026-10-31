package co.dazma.api.security.infrastructure.configuration;

import co.dazma.api.security.domain.InvalidIdentityTokenException;
import co.dazma.api.security.infrastructure.input.errors.ApiAccessDeniedHandler;
import co.dazma.api.security.infrastructure.input.errors.ApiAuthenticationEntryPoint;
import co.dazma.api.security.infrastructure.input.errors.SecurityProblemWriter;
import co.dazma.api.security.infrastructure.input.filters.FirebaseAuthenticationFilter;
import co.dazma.api.security.services.input.dto.AuthenticateIdentityInputDto;
import co.dazma.api.security.services.input.dto.AuthenticateIdentityOutputDto;
import co.dazma.api.security.services.input.ports.AuthenticateIdentityInputPort;
import co.dazma.api.users.domain.Role;
import co.dazma.api.users.domain.State;
import co.dazma.api.users.infrastructure.input.controllers.UserController;
import co.dazma.api.users.services.input.dto.RegisterUserInputDto;
import co.dazma.api.users.services.input.dto.RegisterUserOutputDto;
import co.dazma.api.users.services.input.ports.GetMyProfileInputPort;
import co.dazma.api.users.services.input.ports.RegisterUserInputPort;
import co.dazma.api.shared.infrastructure.input.errors.ApiProblemFactory;
import co.dazma.api.shared.infrastructure.input.errors.ErrorMessageResolver;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.boot.security.autoconfigure.SecurityAutoConfiguration;
import org.springframework.boot.security.autoconfigure.web.servlet.ServletWebSecurityAutoConfiguration;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockCookie;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import static org.hamcrest.Matchers.containsString;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = {
        UserController.class,
        SecurityConfigurationTest.HealthController.class,
        SecurityConfigurationTest.PublicationDetailController.class
})
@Import({
        SecurityConfiguration.class,
        SecurityConfigurationTest.FilterTestConfiguration.class,
        ApiAuthenticationEntryPoint.class,
        ApiAccessDeniedHandler.class,
        SecurityProblemWriter.class,
        ApiProblemFactory.class,
        ErrorMessageResolver.class
})
@ImportAutoConfiguration({
        SecurityAutoConfiguration.class,
        ServletWebSecurityAutoConfiguration.class
})
class SecurityConfigurationTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private RegisterUserInputPort registerUserInputPort;

    @MockitoBean
    private GetMyProfileInputPort getMyProfileInputPort;

    @MockitoBean
    private AuthenticateIdentityInputPort authenticateIdentityInputPort;

    @Test
    void shouldAllowHealthEndpointWithoutAuthentication() throws Exception {
        mockMvc.perform(get("/actuator/health"))
                .andExpect(status().isOk());

        verifyNoInteractions(authenticateIdentityInputPort);
    }

    @Test
    void shouldRejectProtectedEndpointWithoutToken() throws Exception {
        mockMvc.perform(post("/users/api/v1/users")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(validRegistrationBody()))
                .andExpect(status().isUnauthorized())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_PROBLEM_JSON))
                .andExpect(jsonPath("$.status").value(401))
                .andExpect(jsonPath("$.title").value("Autenticación requerida"))
                .andExpect(jsonPath("$.detail").value(
                        "Se requiere autenticación para acceder a este recurso."
                ))
                .andExpect(jsonPath("$.code").value("AUTHENTICATION_REQUIRED"))
                .andExpect(jsonPath("$.instance").value("/users/api/v1/users"));

        verifyNoInteractions(registerUserInputPort);
        verifyNoInteractions(authenticateIdentityInputPort);
    }

    @Test
    void shouldNotAuthenticateUsingABearerHeader() throws Exception {
        mockMvc.perform(post("/users/api/v1/users")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer valid-token")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(validRegistrationBody()))
                .andExpect(status().isUnauthorized())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_PROBLEM_JSON))
                .andExpect(jsonPath("$.code").value("AUTHENTICATION_REQUIRED"));

        verifyNoInteractions(registerUserInputPort);
        verifyNoInteractions(authenticateIdentityInputPort);
    }

    @Test
    void shouldRejectAStateChangingRequestWithoutCsrfToken() throws Exception {
        mockMvc.perform(post("/users/api/v1/users")
                        .cookie(new MockCookie("dazma_session", "valid-token"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(validRegistrationBody()))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.code").value("ACCESS_DENIED"));

        verifyNoInteractions(registerUserInputPort);
    }

    @Test
    void shouldRejectProtectedEndpointWhenTokenIsInvalid() throws Exception {
        when(authenticateIdentityInputPort.execute(new AuthenticateIdentityInputDto("invalid-token", true)))
                .thenThrow(new InvalidIdentityTokenException());

        mockMvc.perform(post("/users/api/v1/users")
                        .cookie(new MockCookie("dazma_session", "invalid-token"))
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(validRegistrationBody()))
                .andExpect(status().isUnauthorized())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_PROBLEM_JSON))
                .andExpect(jsonPath("$.status").value(401))
                .andExpect(jsonPath("$.title").value("Autenticación requerida"))
                .andExpect(jsonPath("$.detail").value("El token de identidad no es válido."))
                .andExpect(jsonPath("$.code").value("IDENTITY_TOKEN_INVALID"))
                .andExpect(jsonPath("$.instance").value("/users/api/v1/users"));

        verifyNoInteractions(registerUserInputPort);
    }

    @Test
    void shouldUseFirebaseUidAsPrincipalWhenTokenIsValid() throws Exception {
        RegisterUserInputDto expectedInput = new RegisterUserInputDto(
                "Ana",
                "3001234567",
                "05615",
                "firebase-user-123",
                "ana@dazma.store"
        );
        when(authenticateIdentityInputPort.execute(new AuthenticateIdentityInputDto("valid-token", true)))
                .thenReturn(new AuthenticateIdentityOutputDto("firebase-user-123", "ana@dazma.store", true));
        when(registerUserInputPort.execute(expectedInput)).thenReturn(new RegisterUserOutputDto(
                java.util.UUID.randomUUID(), "Ana", "3001234567", "05615", State.ACTIVE, Role.USER
        ));

        mockMvc.perform(post("/users/api/v1/users")
                        .cookie(new MockCookie("dazma_session", "valid-token"))
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(validRegistrationBody()))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("Ana"))
                .andExpect(jsonPath("$.role").value("USER"));

        verify(registerUserInputPort).execute(expectedInput);
    }

    @Test
    void shouldAllowPublicationDetailWithoutAuthentication() throws Exception {
        mockMvc.perform(get("/publications/api/v1/publications/{publicationId}", java.util.UUID.randomUUID()))
                .andExpect(status().isOk());

        verifyNoInteractions(authenticateIdentityInputPort);
    }

    @Test
    void shouldAllowMunicipalityCatalogWithoutAuthentication() throws Exception {
        mockMvc.perform(get("/shared/api/v1/municipalities"))
                .andExpect(status().isOk());

        verifyNoInteractions(authenticateIdentityInputPort);
    }

    @Test
    void shouldServeThePublicListAnonymouslyAndExpireAnInvalidSessionCookie() throws Exception {
        when(authenticateIdentityInputPort.execute(new AuthenticateIdentityInputDto("expired-token", false)))
                .thenThrow(new InvalidIdentityTokenException());

        mockMvc.perform(get("/publications/api/v1/publications")
                        .cookie(new MockCookie("dazma_session", "expired-token")))
                .andExpect(status().isOk())
                .andExpect(header().string(HttpHeaders.SET_COOKIE, containsString("Max-Age=0")));
    }

    @Test
    void shouldAllowPublicationListWithoutAuthentication() throws Exception {
        mockMvc.perform(get("/publications/api/v1/publications"))
                .andExpect(status().isOk());

        verifyNoInteractions(authenticateIdentityInputPort);
    }

    @Test
    void shouldNotHideErrorDispatchesBehindAuthentication() throws Exception {
        mockMvc.perform(get("/error").with(request -> {
                    request.setDispatcherType(jakarta.servlet.DispatcherType.ERROR);
                    return request;
                }))
                .andExpect(result -> org.junit.jupiter.api.Assertions.assertNotEquals(
                        401,
                        result.getResponse().getStatus()
                ));

        verifyNoInteractions(authenticateIdentityInputPort);
    }

    @Test
    void shouldReturnForbiddenProblemWhenAuthenticatedUserAccessesDeniedRoute() throws Exception {
        when(authenticateIdentityInputPort.execute(new AuthenticateIdentityInputDto("valid-token", false)))
                .thenReturn(new AuthenticateIdentityOutputDto("firebase-user-123", "ana@dazma.store", true));

        mockMvc.perform(get("/denied")
                        .cookie(new MockCookie("dazma_session", "valid-token")))
                .andExpect(status().isForbidden())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_PROBLEM_JSON))
                .andExpect(jsonPath("$.status").value(403))
                .andExpect(jsonPath("$.title").value("Operación no permitida"))
                .andExpect(jsonPath("$.detail").value(
                        "El usuario autenticado no tiene permiso para realizar esta operación."
                ))
                .andExpect(jsonPath("$.code").value("ACCESS_DENIED"))
                .andExpect(jsonPath("$.instance").value("/denied"));
    }

    private static String validRegistrationBody() {
        return """
                {
                  "name": "Ana",
                  "phone": "3001234567",
                  "municipality": "05615"
                }
                """;
    }

    @RestController
    public static class HealthController {

        @GetMapping("/actuator/health")
        String health() {
            return "UP";
        }
    }

    @RestController
    public static class PublicationDetailController {

        @GetMapping(SecurityConfiguration.PUBLIC_PUBLICATION_DETAIL_PATH)
        String detail() {
            return "{}";
        }

        @GetMapping(SecurityConfiguration.PUBLIC_PUBLICATION_LIST_PATH)
        String list() {
            return "{}";
        }

        @GetMapping(SecurityConfiguration.PUBLIC_MUNICIPALITY_LIST_PATH)
        String municipalities() {
            return "[]";
        }
    }

    @TestConfiguration(proxyBeanMethods = false)
    static class FilterTestConfiguration {

        @Bean
        FirebaseAuthenticationFilter firebaseAuthenticationFilter(
                AuthenticateIdentityInputPort authenticateIdentityInputPort
        ) {
            return new FirebaseAuthenticationFilter(authenticateIdentityInputPort);
        }

        @Bean
        FilterRegistrationBean<FirebaseAuthenticationFilter> firebaseAuthenticationFilterRegistration(
                FirebaseAuthenticationFilter filter
        ) {
            FilterRegistrationBean<FirebaseAuthenticationFilter> registration =
                    new FilterRegistrationBean<>(filter);
            registration.setEnabled(false);
            return registration;
        }

        @Bean
        HealthController healthController() {
            return new HealthController();
        }

        @Bean
        PublicationDetailController publicationDetailController() {
            return new PublicationDetailController();
        }
    }
}
