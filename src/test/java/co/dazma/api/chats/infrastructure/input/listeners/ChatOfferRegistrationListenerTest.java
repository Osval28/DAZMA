package co.dazma.api.chats.infrastructure.input.listeners;

import co.dazma.api.chats.services.input.dto.RegisterChatOfferInputDto;
import co.dazma.api.chats.services.input.ports.RegisterChatOfferInputPort;
import co.dazma.api.publications.events.PublicationCreated;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.UUID;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

class ChatOfferRegistrationListenerTest {

    @Test
    void shouldCopyTheCreatedPublicationAsAChatOffer() {
        RegisterChatOfferInputPort inputPort = mock(RegisterChatOfferInputPort.class);
        UUID publicationId = UUID.randomUUID();
        UUID ownerId = UUID.randomUUID();

        new ChatOfferRegistrationListener(inputPort)
                .on(new PublicationCreated(publicationId, ownerId, Instant.parse("2026-11-01T12:00:00Z")));

        verify(inputPort).execute(new RegisterChatOfferInputDto(publicationId, ownerId));
    }
}
