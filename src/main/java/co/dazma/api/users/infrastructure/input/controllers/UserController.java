package co.dazma.api.users.infrastructure.input.controllers;

import co.dazma.api.users.domain.User;
import co.dazma.api.users.infrastructure.input.dto.RegisterUserRequestDto;
import co.dazma.api.users.infrastructure.input.dto.UserResponseDto;
import co.dazma.api.users.services.input.ports.RegisterUserUseCase;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final RegisterUserUseCase registerUserUseCase;

    public UserController(RegisterUserUseCase registerUserUseCase) {
        this.registerUserUseCase = registerUserUseCase;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public UserResponseDto registerUser(
            @RequestBody @Valid RegisterUserRequestDto request,
            Principal principal
    ) {
        User registeredUser = registerUserUseCase.registerUser(
                request.name(),
                request.phone(),
                request.municipality(),
                principal.getName()
        );

        return UserResponseDto.fromModel(registeredUser);
    }
}