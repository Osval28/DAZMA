package co.dazma.api.users.services.usecases.impl;

import co.dazma.api.shared.domain.errors.InvalidMunicipalityException;
import co.dazma.api.shared.domain.errors.MunicipalityErrorCode;
import co.dazma.api.shared.services.output.ports.MunicipalityCatalogPort;
import co.dazma.api.users.domain.User;
import co.dazma.api.users.domain.UserAlreadyRegisteredException;
import co.dazma.api.users.domain.UserRegistration;
import co.dazma.api.users.events.UserRegistered;
import co.dazma.api.users.services.output.ports.UserEventsPort;
import co.dazma.api.users.services.output.ports.UserRepositoryPort;
import co.dazma.api.users.services.usecases.contract.RegisterUserUseCase;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionOperations;

@Service
public class RegisterUserService implements RegisterUserUseCase {

    private static final Logger log = LoggerFactory.getLogger(RegisterUserService.class);

    private final UserRepositoryPort userRepository;
    private final MunicipalityCatalogPort municipalityCatalog;
    private final UserEventsPort userEvents;
    private final TransactionOperations transaction;

    public RegisterUserService(
            UserRepositoryPort userRepository,
            MunicipalityCatalogPort municipalityCatalog,
            UserEventsPort userEvents,
            TransactionOperations transaction
    ) {
        this.userRepository = userRepository;
        this.municipalityCatalog = municipalityCatalog;
        this.userEvents = userEvents;
        this.transaction = transaction;
    }

    @Override
    public User execute(UserRegistration registration) {
        if (!municipalityCatalog.exists(registration.municipality())) {
            throw new InvalidMunicipalityException(MunicipalityErrorCode.MUNICIPALITY_NOT_FOUND);
        }

        if (userRepository.findByExternalId(registration.externalId()).isPresent()) {
            throw new UserAlreadyRegisteredException();
        }

        User user = User.register(
                registration.name(),
                registration.phone(),
                registration.municipality(),
                registration.externalId(),
                registration.email()
        );

        User savedUser = transaction.execute(status -> saveAndAnnounce(user));
        log.atInfo()
                .addKeyValue("event.name", "user.registered")
                .addKeyValue("user.id", savedUser.getId())
                .addKeyValue("user.state", savedUser.getState().name())
                .log("Usuario registrado");
        return savedUser;
    }

    private User saveAndAnnounce(User user) {
        User savedUser = userRepository.save(user);
        userEvents.publish(new UserRegistered(
                savedUser.getId(),
                savedUser.getExternalId(),
                savedUser.getName().value(),
                savedUser.getEmail().value(),
                savedUser.getState().name()
        ));
        return savedUser;
    }
}
