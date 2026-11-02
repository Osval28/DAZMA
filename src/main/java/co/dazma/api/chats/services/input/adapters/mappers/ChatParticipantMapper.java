package co.dazma.api.chats.services.input.adapters.mappers;

import co.dazma.api.chats.domain.ChatParticipant;
import co.dazma.api.chats.domain.ChatParticipantState;
import co.dazma.api.chats.services.input.dto.RegisterChatParticipantInputDto;
import co.dazma.api.shared.services.input.mappers.InputMapper;
import org.springframework.stereotype.Component;

@Component
public class ChatParticipantMapper implements InputMapper<RegisterChatParticipantInputDto, ChatParticipant> {

    @Override
    public ChatParticipant toDomain(RegisterChatParticipantInputDto input) {
        return new ChatParticipant(
                input.userId(),
                input.externalId(),
                input.name(),
                ChatParticipantState.valueOf(input.state())
        );
    }
}
