package co.dazma.api.chats.services.input.adapters;

import co.dazma.api.chats.domain.ChatRoom;
import co.dazma.api.chats.domain.ChatRoomOpening;
import co.dazma.api.chats.services.input.adapters.mappers.ChatRoomOpeningMapper;
import co.dazma.api.chats.services.input.dto.OpenChatRoomInputDto;
import co.dazma.api.chats.services.input.dto.OpenChatRoomOutputDto;
import co.dazma.api.chats.services.usecases.contract.OpenChatRoomUseCase;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class OpenChatRoomInputAdapterTest {

    @Test
    void shouldAnswerWithTheIdOfTheOpenedRoom() {
        OpenChatRoomUseCase useCase = mock(OpenChatRoomUseCase.class);
        UUID publicationId = UUID.randomUUID();
        ChatRoom room = ChatRoom.open(publicationId, UUID.randomUUID(), UUID.randomUUID(), Instant.parse("2026-11-01T12:00:00Z"));
        when(useCase.execute(new ChatRoomOpening("firebase-123", true, publicationId))).thenReturn(room);

        OpenChatRoomOutputDto output = new OpenChatRoomInputAdapter(useCase, new ChatRoomOpeningMapper())
                .execute(new OpenChatRoomInputDto("firebase-123", true, publicationId));

        assertEquals(new OpenChatRoomOutputDto(room.getId()), output);
    }
}
