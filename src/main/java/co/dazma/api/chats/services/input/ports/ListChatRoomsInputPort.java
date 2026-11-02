package co.dazma.api.chats.services.input.ports;

import co.dazma.api.chats.services.input.dto.ChatRoomSummaryOutputDto;
import co.dazma.api.chats.services.input.dto.ListChatRoomsInputDto;
import co.dazma.api.shared.services.input.ports.InputPortWithReturn;

import java.util.List;

public interface ListChatRoomsInputPort extends InputPortWithReturn<ListChatRoomsInputDto, List<ChatRoomSummaryOutputDto>> {
}
