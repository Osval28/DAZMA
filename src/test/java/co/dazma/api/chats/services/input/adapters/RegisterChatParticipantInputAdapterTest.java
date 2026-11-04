package co.dazma.api.chats.services.input.adapters;

import co.dazma.api.chats.domain.models.ChatParticipant;
import co.dazma.api.chats.domain.enums.ChatParticipantState;
import co.dazma.api.chats.services.input.adapters.mappers.ChatParticipantMapper;
import co.dazma.api.chats.services.input.dto.RegisterChatParticipantInputDto;
import co.dazma.api.chats.services.usecases.contract.RegisterChatParticipantUseCase;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

class RegisterChatParticipantInputAdapterTest {

    @Test
    void shouldGiveTheUseCaseTheParticipantAsDomain() {
        RegisterChatParticipantUseCase useCase = mock(RegisterChatParticipantUseCase.class);
        UUID userId = UUID.randomUUID();

        new RegisterChatParticipantInputAdapter(useCase, new ChatParticipantMapper())
                .execute(new RegisterChatParticipantInputDto(userId, "firebase-123", "Ana", "SUSPENDED"));

        verify(useCase).execute(new ChatParticipant(userId, "firebase-123", "Ana", ChatParticipantState.SUSPENDED));
    }
}
