package co.dazma.api.chats.services.input.adapters;

import co.dazma.api.chats.services.input.adapters.mappers.ChatAccessMapper;
import co.dazma.api.chats.services.input.dto.IssueChatTokenInputDto;
import co.dazma.api.chats.services.input.dto.IssueChatTokenOutputDto;
import co.dazma.api.chats.services.input.ports.IssueChatTokenInputPort;
import co.dazma.api.chats.services.usecases.contract.IssueChatTokenUseCase;
import org.springframework.stereotype.Service;

@Service
public class IssueChatTokenInputAdapter implements IssueChatTokenInputPort {

    private final IssueChatTokenUseCase useCase;
    private final ChatAccessMapper mapper;

    public IssueChatTokenInputAdapter(IssueChatTokenUseCase useCase, ChatAccessMapper mapper) {
        this.useCase = useCase;
        this.mapper = mapper;
    }

    @Override
    public IssueChatTokenOutputDto execute(IssueChatTokenInputDto input) {
        return mapper.toOutput(useCase.execute(mapper.toDomain(input)));
    }
}
