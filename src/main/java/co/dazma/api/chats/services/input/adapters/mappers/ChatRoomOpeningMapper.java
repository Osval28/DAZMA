package co.dazma.api.chats.services.input.adapters.mappers;

import co.dazma.api.chats.domain.ChatRoom;
import co.dazma.api.chats.domain.ChatRoomOpening;
import co.dazma.api.chats.services.input.dto.OpenChatRoomInputDto;
import co.dazma.api.chats.services.input.dto.OpenChatRoomOutputDto;
import co.dazma.api.shared.services.input.mappers.InputMapper;
import co.dazma.api.shared.services.input.mappers.OutputMapper;
import org.springframework.stereotype.Component;

@Component
public class ChatRoomOpeningMapper implements InputMapper<OpenChatRoomInputDto, ChatRoomOpening>,
        OutputMapper<ChatRoom, OpenChatRoomOutputDto> {

    @Override
    public ChatRoomOpening toDomain(OpenChatRoomInputDto input) {
        return new ChatRoomOpening(input.externalId(), input.emailVerified(), input.publicationId());
    }

    @Override
    public OpenChatRoomOutputDto toOutput(ChatRoom room) {
        return new OpenChatRoomOutputDto(room.getId());
    }
}
