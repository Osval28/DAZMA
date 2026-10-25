package co.dazma.api.users.infrastructure.input.controllers;

import co.dazma.api.security.AuthenticatedUser;
import co.dazma.api.users.domain.User;
import co.dazma.api.users.infrastructure.input.dto.RegisterUserRequestDto;
import co.dazma.api.users.infrastructure.input.dto.UserResponseDto;
import co.dazma.api.users.services.input.ports.RegisterUserUseCase;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

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
            @AuthenticationPrincipal AuthenticatedUser authenticatedUser
    ) {
        User registeredUser = registerUserUseCase.registerUser(
                request.name(),
                request.phone(),
                request.municipality(),
                authenticatedUser.getName(),
                authenticatedUser.email()
        );

        return UserResponseDto.fromModel(registeredUser);
    }
}