package co.dazma.api.chats.services.input.adapters;

import co.dazma.api.chats.services.input.adapters.mappers.ChatRoomSummaryMapper;
import co.dazma.api.chats.services.input.dto.ChatRoomSummaryOutputDto;
import co.dazma.api.chats.services.input.dto.ListChatRoomsInputDto;
import co.dazma.api.chats.services.input.ports.ListChatRoomsInputPort;
import co.dazma.api.chats.services.usecases.contract.ListChatRoomsUseCase;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ListChatRoomsInputAdapter implements ListChatRoomsInputPort {

    private final ListChatRoomsUseCase useCase;
    private final ChatRoomSummaryMapper mapper;

    public ListChatRoomsInputAdapter(ListChatRoomsUseCase useCase, ChatRoomSummaryMapper mapper) {
        this.useCase = useCase;
        this.mapper = mapper;
    }

    @Override
    public List<ChatRoomSummaryOutputDto> execute(ListChatRoomsInputDto input) {
        return useCase.execute(mapper.toDomain(input)).stream().map(mapper::toOutput).toList();
    }
}
