package co.dazma.api.chats.services.input.adapters;

import co.dazma.api.chats.services.input.adapters.mappers.ChatRoomSummaryMapper;
import co.dazma.api.chats.services.input.dto.ChatRoomSummaryOutputDto;
import co.dazma.api.chats.services.input.dto.ListChatRoomsInputDto;
import co.dazma.api.chats.services.usecases.contract.ListChatRoomsUseCase;
import co.dazma.api.chats.services.usecases.models.ChatRoomSummary;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class ListChatRoomsInputAdapterTest {

    @Test
    void shouldAnswerWithOneSummaryPerRoom() {
        ListChatRoomsUseCase useCase = mock(ListChatRoomsUseCase.class);
        UUID roomId = UUID.randomUUID();
        when(useCase.execute("firebase-123")).thenReturn(List.of(new ChatRoomSummary(roomId, "Luis")));

        List<ChatRoomSummaryOutputDto> output = new ListChatRoomsInputAdapter(useCase, new ChatRoomSummaryMapper())
                .execute(new ListChatRoomsInputDto("firebase-123"));

        assertEquals(List.of(new ChatRoomSummaryOutputDto(roomId, "Luis")), output);
    }
}
