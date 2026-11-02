package co.dazma.api.chats.services.input.adapters.mappers;

import co.dazma.api.chats.services.input.dto.ChatRoomSummaryOutputDto;
import co.dazma.api.chats.services.input.dto.ListChatRoomsInputDto;
import co.dazma.api.chats.services.usecases.models.ChatRoomSummary;
import co.dazma.api.shared.services.input.mappers.InputMapper;
import co.dazma.api.shared.services.input.mappers.OutputMapper;
import org.springframework.stereotype.Component;

@Component
public class ChatRoomSummaryMapper implements InputMapper<ListChatRoomsInputDto, String>,
        OutputMapper<ChatRoomSummary, ChatRoomSummaryOutputDto> {

    @Override
    public String toDomain(ListChatRoomsInputDto input) {
        return input.externalId();
    }

    @Override
    public ChatRoomSummaryOutputDto toOutput(ChatRoomSummary summary) {
        return new ChatRoomSummaryOutputDto(summary.roomId(), summary.participantName());
    }
}
