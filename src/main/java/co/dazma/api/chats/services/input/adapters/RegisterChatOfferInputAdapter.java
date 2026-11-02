package co.dazma.api.chats.services.input.adapters;

import co.dazma.api.chats.services.input.adapters.mappers.ChatOfferMapper;
import co.dazma.api.chats.services.input.dto.RegisterChatOfferInputDto;
import co.dazma.api.chats.services.input.ports.RegisterChatOfferInputPort;
import co.dazma.api.chats.services.usecases.contract.RegisterChatOfferUseCase;
import org.springframework.stereotype.Service;

@Service
public class RegisterChatOfferInputAdapter implements RegisterChatOfferInputPort {

    private final RegisterChatOfferUseCase useCase;
    private final ChatOfferMapper mapper;

    public RegisterChatOfferInputAdapter(RegisterChatOfferUseCase useCase, ChatOfferMapper mapper) {
        this.useCase = useCase;
        this.mapper = mapper;
    }

    @Override
    public void execute(RegisterChatOfferInputDto input) {
        useCase.execute(mapper.toDomain(input));
    }
}
