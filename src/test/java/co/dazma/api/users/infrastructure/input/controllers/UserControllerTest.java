package co.dazma.api.users.infrastructure.input.controllers;

import co.dazma.api.security.AuthenticatedUser;
import co.dazma.api.shared.infrastructure.input.errors.ApiProblemFactory;
import co.dazma.api.shared.infrastructure.input.errors.ErrorMessageResolver;
import co.dazma.api.shared.infrastructure.input.errors.GlobalExceptionHandler;
import co.dazma.api.shared.domain.Municipality;
import co.dazma.api.users.domain.Email;
import co.dazma.api.users.domain.Phone;
import co.dazma.api.users.domain.Name;
import co.dazma.api.users.domain.Role;
import co.dazma.api.users.domain.State;
import co.dazma.api.users.domain.User;
import co.dazma.api.users.domain.UserNotFoundException;
import co.dazma.api.users.services.input.dto.GetMyProfileInputDto;
import co.dazma.api.users.services.input.dto.GetMyProfileOutputDto;
import co.dazma.api.users.services.input.ports.GetMyProfileUseCase;
import co.dazma.api.users.services.input.ports.RegisterUserUseCase;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.context.support.ResourceBundleMessageSource;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.method.annotation.AuthenticationPrincipalArgumentResolver;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.UUID;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class UserControllerTest {

    private RegisterUserUseCase registerUserUseCase;
    private GetMyProfileUseCase getMyProfileUseCase;
    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        registerUserUseCase = mock(RegisterUserUseCase.class);
        getMyProfileUseCase = mock(GetMyProfileUseCase.class);
        UserController controller = new UserController(registerUserUseCase, getMyProfileUseCase);
        ResourceBundleMessageSource messageSource = new ResourceBundleMessageSource();
        messageSource.setBasename("messages");
        messageSource.setDefaultEncoding(StandardCharsets.UTF_8.name());
        GlobalExceptionHandler exceptionHandler =
                new GlobalExceptionHandler(new ApiProblemFactory(new ErrorMessageResolver(messageSource)));
        mockMvc = MockMvcBuilders.standaloneSetup(controller)
                .setControllerAdvice(exceptionHandler)
                .setCustomArgumentResolvers(new AuthenticationPrincipalArgumentResolver())
                .build();
        AuthenticatedUser authenticatedUser =
                new AuthenticatedUser("firebase-123", "ana@dazma.store", true);
        SecurityContextHolder.getContext().setAuthentication(
                UsernamePasswordAuthenticationToken.authenticated(authenticatedUser, null, List.of())
        );
    }

    @AfterEach
    void clearSecurityContext() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void shouldRegisterUserUsingTheAuthenticatedExternalId() throws Exception {
        User registeredUser = User.register(new Name("Ana"),
                new Phone("3001234567"),
                new Municipality("Rionegro"),
                "firebase-123",
                new Email("ana@dazma.store")
        );
        when(registerUserUseCase.registerUser(
                "Ana",
                "3001234567",
                "Rionegro",
                "firebase-123",
                "ana@dazma.store"
        )).thenReturn(registeredUser);

        mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "name": "Ana",
                                  "phone": "3001234567",
                                  "municipality": "Rionegro"
                                }
                                """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(registeredUser.getId().toString()))
                .andExpect(jsonPath("$.name").value("Ana"))
                .andExpect(jsonPath("$.phone").value("3001234567"))
                .andExpect(jsonPath("$.municipality").value("Rionegro"))
                .andExpect(jsonPath("$.state").value("ACTIVE"))
                .andExpect(jsonPath("$.role").value("USER"));

        verify(registerUserUseCase).registerUser(
                "Ana",
                "3001234567",
                "Rionegro",
                "firebase-123",
                "ana@dazma.store"
        );
    }

    @Test
    void shouldReturnTheProfileOfTheAuthenticatedUserWithTheEmailVerificationFromTheSession() throws Exception {
        UUID id = UUID.randomUUID();
        when(getMyProfileUseCase.get(new GetMyProfileInputDto("firebase-123")))
                .thenReturn(new GetMyProfileOutputDto(id, "Ana", "3001234567", "Rionegro",
                        "ana@dazma.store", State.ACTIVE, Role.USER));

        mockMvc.perform(get("/api/users/me"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(id.toString()))
                .andExpect(jsonPath("$.name").value("Ana"))
                .andExpect(jsonPath("$.email").value("ana@dazma.store"))
                .andExpect(jsonPath("$.emailVerified").value(true))
                .andExpect(jsonPath("$.state").value("ACTIVE"))
                .andExpect(jsonPath("$.role").value("USER"));
    }

    @Test
    void shouldAnswerNotFoundWhenTheAuthenticatedUserHasNoProfile() throws Exception {
        when(getMyProfileUseCase.get(new GetMyProfileInputDto("firebase-123")))
                .thenThrow(new UserNotFoundException());

        mockMvc.perform(get("/api/users/me"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value("USER_NOT_FOUND"));
    }
}
