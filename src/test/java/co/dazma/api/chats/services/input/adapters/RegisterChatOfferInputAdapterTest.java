package co.dazma.api.chats.services.input.adapters;

import co.dazma.api.chats.domain.models.ChatOffer;
import co.dazma.api.chats.services.input.adapters.mappers.ChatOfferMapper;
import co.dazma.api.chats.services.input.dto.RegisterChatOfferInputDto;
import co.dazma.api.chats.services.usecases.contract.RegisterChatOfferUseCase;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

class RegisterChatOfferInputAdapterTest {

    @Test
    void shouldGiveTheUseCaseTheOfferAsDomain() {
        RegisterChatOfferUseCase useCase = mock(RegisterChatOfferUseCase.class);
        UUID publicationId = UUID.randomUUID();
        UUID ownerId = UUID.randomUUID();

        new RegisterChatOfferInputAdapter(useCase, new ChatOfferMapper())
                .execute(new RegisterChatOfferInputDto(publicationId, ownerId));

        verify(useCase).execute(new ChatOffer(publicationId, ownerId));
    }
}
