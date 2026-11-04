package co.dazma.api.chats.services.input.adapters;

import co.dazma.api.chats.services.input.adapters.mappers.ChatAccessMapper;
import co.dazma.api.chats.services.input.dto.IssueChatTokenInputDto;
import co.dazma.api.chats.services.input.dto.IssueChatTokenOutputDto;
import co.dazma.api.chats.services.output.models.ChatTokenRequest;
import co.dazma.api.chats.services.usecases.contract.IssueChatTokenUseCase;
import co.dazma.api.chats.services.usecases.models.IssueChatTokenCommand;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class IssueChatTokenInputAdapterTest {

    @Test
    void shouldAnswerWithTheSignedTokenRequest() {
        IssueChatTokenUseCase useCase = mock(IssueChatTokenUseCase.class);
        UUID roomId = UUID.randomUUID();
        when(useCase.execute(new IssueChatTokenCommand("firebase-123", roomId))).thenReturn(
                new ChatTokenRequest("key", 3_600_000, 1_700_000_000_000L, "{}", "client", "nonce", "mac")
        );

        IssueChatTokenOutputDto output = new IssueChatTokenInputAdapter(useCase, new ChatAccessMapper())
                .execute(new IssueChatTokenInputDto("firebase-123", roomId));

        assertEquals(
                new IssueChatTokenOutputDto("key", 3_600_000, 1_700_000_000_000L, "{}", "client", "nonce", "mac"),
                output
        );
    }
}
