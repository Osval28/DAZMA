package co.dazma.api.users.infrastructure.input.controllers;

import co.dazma.api.shared.infrastructure.security.CurrentUser;
import co.dazma.api.users.infrastructure.input.dto.MyProfileResponseDto;
import co.dazma.api.users.infrastructure.input.dto.PhoneRulesResponseDto;
import co.dazma.api.users.infrastructure.input.dto.RegisterUserRequestDto;
import co.dazma.api.users.infrastructure.input.dto.UserResponseDto;
import co.dazma.api.users.services.input.dto.GetMyProfileInputDto;
import co.dazma.api.users.services.input.dto.GetMyProfileOutputDto;
import co.dazma.api.users.services.input.dto.RegisterUserInputDto;
import co.dazma.api.users.services.input.dto.RegisterUserOutputDto;
import co.dazma.api.users.services.input.ports.GetMyProfileInputPort;
import co.dazma.api.users.services.input.ports.GetPhoneRulesInputPort;
import co.dazma.api.users.services.input.ports.RegisterUserInputPort;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.CacheControl;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/users/api/v1/users")
public class UserController {

    private final RegisterUserInputPort registerUserInputPort;
    private final GetMyProfileInputPort getMyProfileInputPort;
    private final GetPhoneRulesInputPort getPhoneRulesInputPort;

    public UserController(RegisterUserInputPort registerUserInputPort, GetMyProfileInputPort getMyProfileInputPort,
                          GetPhoneRulesInputPort getPhoneRulesInputPort) {
        this.registerUserInputPort = registerUserInputPort;
        this.getMyProfileInputPort = getMyProfileInputPort;
        this.getPhoneRulesInputPort = getPhoneRulesInputPort;
    }

    @GetMapping("/rules")
    public ResponseEntity<PhoneRulesResponseDto> rules() {
        return ResponseEntity.ok().cacheControl(CacheControl.noStore())
                .body(PhoneRulesResponseDto.from(getPhoneRulesInputPort.execute()));
    }

    @GetMapping("/me")
    public MyProfileResponseDto getMyProfile(@AuthenticationPrincipal CurrentUser currentUser) {
        GetMyProfileOutputDto profile = getMyProfileInputPort.execute(
                new GetMyProfileInputDto(currentUser.getName())
        );
        return MyProfileResponseDto.from(profile, currentUser.emailVerified());
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public UserResponseDto registerUser(
            @RequestBody @Valid RegisterUserRequestDto request,
            @AuthenticationPrincipal CurrentUser currentUser
    ) {
        RegisterUserOutputDto registeredUser = registerUserInputPort.execute(new RegisterUserInputDto(
                request.name(),
                request.phone(),
                request.municipality(),
                currentUser.getName(),
                currentUser.email()
        ));

        return UserResponseDto.from(registeredUser);
    }
}
