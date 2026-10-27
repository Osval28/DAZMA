package co.dazma.api.publications.infrastructure.input.listeners;

import co.dazma.api.publications.services.input.dto.RegisterPublicationOwnerInputDto;
import co.dazma.api.publications.services.input.ports.RegisterPublicationOwnerInputPort;
import co.dazma.api.users.events.UserRegistered;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

class PublicationOwnerRegistrationListenerTest {

    @Test
    void shouldCopyTheRegisteredUserAsAPublicationOwner() {
        RegisterPublicationOwnerInputPort inputPort = mock(RegisterPublicationOwnerInputPort.class);
        PublicationOwnerRegistrationListener listener = new PublicationOwnerRegistrationListener(inputPort);
        UUID userId = UUID.randomUUID();

        listener.on(new UserRegistered(userId, "firebase-123", "ana@dazma.store", "ACTIVE"));

        verify(inputPort).execute(new RegisterPublicationOwnerInputDto(userId, "firebase-123", "ACTIVE"));
    }
}
