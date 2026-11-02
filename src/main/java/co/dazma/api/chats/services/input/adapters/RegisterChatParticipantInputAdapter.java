package co.dazma.api.chats.services.input.adapters;

import co.dazma.api.chats.services.input.adapters.mappers.ChatParticipantMapper;
import co.dazma.api.chats.services.input.dto.RegisterChatParticipantInputDto;
import co.dazma.api.chats.services.input.ports.RegisterChatParticipantInputPort;
import co.dazma.api.chats.services.usecases.contract.RegisterChatParticipantUseCase;
import org.springframework.stereotype.Service;

@Service
public class RegisterChatParticipantInputAdapter implements RegisterChatParticipantInputPort {

    private final RegisterChatParticipantUseCase useCase;
    private final ChatParticipantMapper mapper;

    public RegisterChatParticipantInputAdapter(RegisterChatParticipantUseCase useCase, ChatParticipantMapper mapper) {
        this.useCase = useCase;
        this.mapper = mapper;
    }

    @Override
    public void execute(RegisterChatParticipantInputDto input) {
        useCase.execute(mapper.toDomain(input));
    }
}
