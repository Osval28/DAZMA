package co.dazma.api.chats.infrastructure.output.repositories.structured.mapper;

import co.dazma.api.chats.infrastructure.output.repositories.structured.entities.ChatOfferEntity;
import co.dazma.api.chats.infrastructure.output.repositories.structured.jpa.entities.ChatOfferEntityJPA;
import co.dazma.api.shared.infrastructure.JpaMapper;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING,
        unmappedTargetPolicy = ReportingPolicy.ERROR)
public interface ChatOfferMapStructJpaMapper extends JpaMapper<ChatOfferEntityJPA, ChatOfferEntity> {

    @Override
    ChatOfferEntityJPA toJpaEntity(ChatOfferEntity entity);

    @Override
    ChatOfferEntity toEntity(ChatOfferEntityJPA jpaEntity);
}
