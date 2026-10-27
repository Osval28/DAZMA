package co.dazma.api.notifications.services.output.ports;

import java.util.UUID;

public interface RecipientPort {

    String emailOf(UUID userId);
}
