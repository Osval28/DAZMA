package co.dazma.api.chats.infrastructure.output.repositories.structured.mapper;

import co.dazma.api.chats.domain.ChatOffer;
import co.dazma.api.chats.infrastructure.output.repositories.structured.entities.ChatOfferEntity;
import co.dazma.api.shared.infrastructure.DomainMapper;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING,
        unmappedTargetPolicy = ReportingPolicy.ERROR)
public interface ChatOfferMapStructDomainMapper extends DomainMapper<ChatOfferEntity, ChatOffer> {

    @Override
    ChatOffer toDomain(ChatOfferEntity entity);

    @Override
    ChatOfferEntity toEntity(ChatOffer domain);
}
