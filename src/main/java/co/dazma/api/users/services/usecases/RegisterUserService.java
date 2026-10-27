package co.dazma.api.users.services.usecases;

import co.dazma.api.shared.domain.Municipality;
import co.dazma.api.users.domain.Email;
import co.dazma.api.users.domain.Phone;
import co.dazma.api.users.domain.Name;
import co.dazma.api.users.domain.User;
import co.dazma.api.users.domain.UserAlreadyRegisteredException;
import co.dazma.api.users.events.UserRegistered;
import co.dazma.api.users.services.input.ports.RegisterUserUseCase;
import co.dazma.api.users.services.output.ports.UserEventsPort;
import co.dazma.api.users.services.output.ports.UserRepositoryPort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionOperations;

@Service
public class RegisterUserService implements RegisterUserUseCase {

    private final UserRepositoryPort userRepository;
    private final UserEventsPort userEvents;
    private final TransactionOperations transaction;

    public RegisterUserService(
            UserRepositoryPort userRepository,
            UserEventsPort userEvents,
            TransactionOperations transaction
    ) {
        this.userRepository = userRepository;
        this.userEvents = userEvents;
        this.transaction = transaction;
    }

    @Override
    public User registerUser(String name, String phone, String municipality, String externalId, String email) {
        if (userRepository.findByExternalId(externalId).isPresent()) {
            throw new UserAlreadyRegisteredException();
        }

        User user = User.register(new Name(name), new Phone(phone), new Municipality(municipality),
                externalId, new Email(email));

        return transaction.execute(status -> saveAndAnnounce(user));
    }

    private User saveAndAnnounce(User user) {
        User savedUser = userRepository.save(user);
        userEvents.publish(new UserRegistered(savedUser.getId(), savedUser.getEmail().value()));
        return savedUser;
    }
}
