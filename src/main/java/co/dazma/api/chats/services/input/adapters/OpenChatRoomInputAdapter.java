package co.dazma.api.chats.services.input.adapters;

import co.dazma.api.chats.services.input.adapters.mappers.ChatRoomOpeningMapper;
import co.dazma.api.chats.services.input.dto.OpenChatRoomInputDto;
import co.dazma.api.chats.services.input.dto.OpenChatRoomOutputDto;
import co.dazma.api.chats.services.input.ports.OpenChatRoomInputPort;
import co.dazma.api.chats.services.usecases.contract.OpenChatRoomUseCase;
import org.springframework.stereotype.Service;

@Service
public class OpenChatRoomInputAdapter implements OpenChatRoomInputPort {

    private final OpenChatRoomUseCase useCase;
    private final ChatRoomOpeningMapper mapper;

    public OpenChatRoomInputAdapter(OpenChatRoomUseCase useCase, ChatRoomOpeningMapper mapper) {
        this.useCase = useCase;
        this.mapper = mapper;
    }

    @Override
    public OpenChatRoomOutputDto execute(OpenChatRoomInputDto input) {
        return mapper.toOutput(useCase.execute(mapper.toDomain(input)));
    }
}
