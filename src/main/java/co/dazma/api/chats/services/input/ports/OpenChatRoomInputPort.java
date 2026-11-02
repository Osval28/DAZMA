package co.dazma.api.chats.services.input.ports;

import co.dazma.api.chats.services.input.dto.OpenChatRoomInputDto;
import co.dazma.api.chats.services.input.dto.OpenChatRoomOutputDto;
import co.dazma.api.shared.services.input.ports.InputPortWithReturn;

public interface OpenChatRoomInputPort extends InputPortWithReturn<OpenChatRoomInputDto, OpenChatRoomOutputDto> {
}
