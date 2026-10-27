package co.dazma.api.chats.infrastructure.output.repositories.structured.mapper;

import co.dazma.api.chats.infrastructure.output.repositories.structured.entities.ChatParticipantEntity;
import co.dazma.api.chats.infrastructure.output.repositories.structured.jpa.entities.ChatParticipantEntityJPA;
import co.dazma.api.shared.infrastructure.JpaMapper;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING,
        unmappedTargetPolicy = ReportingPolicy.ERROR)
public interface ChatParticipantMapStructJpaMapper extends JpaMapper<ChatParticipantEntityJPA, ChatParticipantEntity> {

    @Override
    ChatParticipantEntityJPA toJpaEntity(ChatParticipantEntity entity);

    @Override
    ChatParticipantEntity toEntity(ChatParticipantEntityJPA jpaEntity);
}