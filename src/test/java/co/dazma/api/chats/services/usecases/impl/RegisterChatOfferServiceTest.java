package co.dazma.api.chats.services.usecases.impl;

import co.dazma.api.chats.domain.ChatOffer;
import co.dazma.api.chats.services.output.ports.ChatOfferRepositoryPort;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

class RegisterChatOfferServiceTest {

    @Test
    void shouldSaveTheOfferInTheLocalCopy() {
        ChatOfferRepositoryPort offerRepository = mock(ChatOfferRepositoryPort.class);
        ChatOffer offer = new ChatOffer(UUID.randomUUID(), UUID.randomUUID());

        new RegisterChatOfferService(offerRepository).execute(offer);

        verify(offerRepository).save(offer);
    }
}
