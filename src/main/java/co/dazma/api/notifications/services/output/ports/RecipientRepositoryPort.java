package co.dazma.api.notifications.services.output.ports;

import co.dazma.api.notifications.domain.Recipient;

import java.util.Optional;
import java.util.UUID;

public interface RecipientRepositoryPort {

    void save(Recipient recipient);

    Optional<Recipient> findByUserId(UUID userId);
}
