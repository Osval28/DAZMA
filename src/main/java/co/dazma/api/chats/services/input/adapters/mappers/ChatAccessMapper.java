package co.dazma.api.chats.services.input.adapters.mappers;

import co.dazma.api.chats.services.input.dto.IssueChatTokenInputDto;
import co.dazma.api.chats.services.input.dto.IssueChatTokenOutputDto;
import co.dazma.api.chats.services.output.models.ChatTokenRequest;
import co.dazma.api.chats.services.usecases.models.IssueChatTokenCommand;
import co.dazma.api.shared.services.input.mappers.InputMapper;
import co.dazma.api.shared.services.input.mappers.OutputMapper;
import org.springframework.stereotype.Component;

@Component
public class ChatAccessMapper implements InputMapper<IssueChatTokenInputDto, IssueChatTokenCommand>,
        OutputMapper<ChatTokenRequest, IssueChatTokenOutputDto> {

    @Override
    public IssueChatTokenCommand toDomain(IssueChatTokenInputDto input) {
        return new IssueChatTokenCommand(input.externalId(), input.roomId());
    }

    @Override
    public IssueChatTokenOutputDto toOutput(ChatTokenRequest tokenRequest) {
        return new IssueChatTokenOutputDto(
                tokenRequest.keyName(),
                tokenRequest.ttl(),
                tokenRequest.timestamp(),
                tokenRequest.capability(),
                tokenRequest.clientId(),
                tokenRequest.nonce(),
                tokenRequest.mac()
        );
    }
}
