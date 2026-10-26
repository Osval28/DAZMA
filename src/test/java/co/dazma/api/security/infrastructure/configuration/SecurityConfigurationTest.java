package co.dazma.api.security.infrastructure.configuration;

import co.dazma.api.users.domain.Email;
import co.dazma.api.users.domain.Phone;
import co.dazma.api.users.domain.Name;
import co.dazma.api.security.domain.AuthenticatedIdentity;
import co.dazma.api.security.domain.InvalidIdentityTokenException;
import co.dazma.api.security.infrastructure.input.errors.ApiAccessDeniedHandler;
import co.dazma.api.security.infrastructure.input.errors.ApiAuthenticationEntryPoint;
import co.dazma.api.security.infrastructure.input.errors.SecurityProblemWriter;
import co.dazma.api.security.infrastructure.input.filters.FirebaseAuthenticationFilter;
import co.dazma.api.security.services.input.ports.AuthenticateIdentityUseCase;
import co.dazma.api.users.domain.User;
import co.dazma.api.users.infrastructure.input.controllers.UserController;
import co.dazma.api.users.services.input.ports.RegisterUserUseCase;
import co.dazma.api.shared.domain.Municipality;
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

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
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
    private RegisterUserUseCase registerUserUseCase;

    @MockitoBean
    private AuthenticateIdentityUseCase authenticateIdentityUseCase;

    @Test
    void shouldAllowHealthEndpointWithoutAuthentication() throws Exception {
        mockMvc.perform(get("/actuator/health"))
                .andExpect(status().isOk());

        verifyNoInteractions(authenticateIdentityUseCase);
    }

    @Test
    void shouldRejectProtectedEndpointWithoutToken() throws Exception {
        mockMvc.perform(post("/api/users")
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
                .andExpect(jsonPath("$.instance").value("/api/users"));

        verifyNoInteractions(registerUserUseCase);
        verifyNoInteractions(authenticateIdentityUseCase);
    }

    @Test
    void shouldNotAuthenticateUsingABearerHeader() throws Exception {
        mockMvc.perform(post("/api/users")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer valid-token")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(validRegistrationBody()))
                .andExpect(status().isUnauthorized())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_PROBLEM_JSON))
                .andExpect(jsonPath("$.code").value("AUTHENTICATION_REQUIRED"));

        verifyNoInteractions(registerUserUseCase);
        verifyNoInteractions(authenticateIdentityUseCase);
    }

    @Test
    void shouldRejectAStateChangingRequestWithoutCsrfToken() throws Exception {
        mockMvc.perform(post("/api/users")
                        .cookie(new MockCookie("dazma_session", "valid-token"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(validRegistrationBody()))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.code").value("ACCESS_DENIED"));

        verifyNoInteractions(registerUserUseCase);
    }

    @Test
    void shouldRejectProtectedEndpointWhenTokenIsInvalid() throws Exception {
        when(authenticateIdentityUseCase.authenticate("invalid-token", true))
                .thenThrow(new InvalidIdentityTokenException());

        mockMvc.perform(post("/api/users")
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
                .andExpect(jsonPath("$.instance").value("/api/users"));

        verifyNoInteractions(registerUserUseCase);
    }

    @Test
    void shouldUseFirebaseUidAsPrincipalWhenTokenIsValid() throws Exception {
        User registeredUser = User.register(new Name("Ana"),
                new Phone("3001234567"),
                new Municipality("Rionegro"),
                "firebase-user-123",
                new Email("ana@dazma.store")
        );
        when(authenticateIdentityUseCase.authenticate("valid-token", true))
                .thenReturn(new AuthenticatedIdentity("firebase-user-123", "ana@dazma.store", true));
        when(registerUserUseCase.registerUser(
                "Ana",
                "3001234567",
                "Rionegro",
                "firebase-user-123",
                "ana@dazma.store"
        )).thenReturn(registeredUser);

        mockMvc.perform(post("/api/users")
                        .cookie(new MockCookie("dazma_session", "valid-token"))
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(validRegistrationBody()))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("Ana"))
                .andExpect(jsonPath("$.role").value("USER"));

        verify(registerUserUseCase).registerUser(
                "Ana",
                "3001234567",
                "Rionegro",
                "firebase-user-123",
                "ana@dazma.store"
        );
    }

    @Test
    void shouldAllowPublicationDetailWithoutAuthentication() throws Exception {
        mockMvc.perform(get("/api/publications/{publicationId}", java.util.UUID.randomUUID()))
                .andExpect(status().isOk());

        verifyNoInteractions(authenticateIdentityUseCase);
    }

    @Test
    void shouldReturnForbiddenProblemWhenAuthenticatedUserAccessesDeniedRoute() throws Exception {
        when(authenticateIdentityUseCase.authenticate("valid-token", false))
                .thenReturn(new AuthenticatedIdentity("firebase-user-123", "ana@dazma.store", true));

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
                  "municipality": "Rionegro"
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
    }

    @TestConfiguration(proxyBeanMethods = false)
    static class FilterTestConfiguration {

        @Bean
        FirebaseAuthenticationFilter firebaseAuthenticationFilter(
                AuthenticateIdentityUseCase authenticateIdentityUseCase,
                ApiAuthenticationEntryPoint authenticationEntryPoint
        ) {
            return new FirebaseAuthenticationFilter(
                    authenticateIdentityUseCase,
                    authenticationEntryPoint
            );
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
