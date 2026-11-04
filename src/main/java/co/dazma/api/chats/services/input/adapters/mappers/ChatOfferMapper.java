package co.dazma.api.chats.services.input.adapters.mappers;

import co.dazma.api.chats.domain.models.ChatOffer;
import co.dazma.api.chats.services.input.dto.RegisterChatOfferInputDto;
import co.dazma.api.shared.services.input.mappers.InputMapper;
import org.springframework.stereotype.Component;

@Component
public class ChatOfferMapper implements InputMapper<RegisterChatOfferInputDto, ChatOffer> {

    @Override
    public ChatOffer toDomain(RegisterChatOfferInputDto input) {
        return new ChatOffer(input.publicationId(), input.ownerId());
    }
}
