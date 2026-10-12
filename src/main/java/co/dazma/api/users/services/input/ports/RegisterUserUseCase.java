package co.dazma.api.users.services.input.ports;

import co.dazma.api.users.domain.User;

public interface RegisterUserUseCase {
    User registerUser(String name, String phone, String municipality, String externalId);
}
