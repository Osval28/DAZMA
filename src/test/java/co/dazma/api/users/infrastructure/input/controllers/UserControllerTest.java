package co.dazma.api.users.infrastructure.input.controllers;

import co.dazma.api.users.domain.Phone;
import co.dazma.api.users.domain.Name;
import co.dazma.api.users.domain.User;
import co.dazma.api.users.services.input.ports.RegisterUserUseCase;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class UserControllerTest {

    private RegisterUserUseCase registerUserUseCase;
    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        registerUserUseCase = mock(RegisterUserUseCase.class);
        UserController controller = new UserController(registerUserUseCase);
        mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
    }

    @Test
    void shouldRegisterUserUsingTheAuthenticatedExternalId() throws Exception {
        User registeredUser = User.register(new Name("Ana"),
                new Phone("3001234567"),
                "Rionegro",
                "firebase-123"
        );
        when(registerUserUseCase.registerUser(
                "Ana",
                "3001234567",
                "Rionegro",
                "firebase-123"
        )).thenReturn(registeredUser);

        mockMvc.perform(post("/api/users")
                        .principal(() -> "firebase-123")
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
                "firebase-123"
        );
    }
}
