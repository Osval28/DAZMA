package co.dazma.api.chats.services.input.ports;

import co.dazma.api.chats.services.input.dto.IssueChatTokenInputDto;
import co.dazma.api.chats.services.input.dto.IssueChatTokenOutputDto;
import co.dazma.api.shared.services.input.ports.InputPortWithReturn;

public interface IssueChatTokenInputPort extends InputPortWithReturn<IssueChatTokenInputDto, IssueChatTokenOutputDto> {
}
