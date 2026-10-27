package co.dazma.api.chats.infrastructure.output.repositories.structured.mapper;

import co.dazma.api.chats.domain.ChatParticipant;
import co.dazma.api.chats.infrastructure.output.repositories.structured.entities.ChatParticipantEntity;
import co.dazma.api.shared.infrastructure.DomainMapper;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING,
        unmappedTargetPolicy = ReportingPolicy.ERROR)
public interface ChatParticipantMapStructDomainMapper extends DomainMapper<ChatParticipantEntity, ChatParticipant> {

    @Override
    ChatParticipant toDomain(ChatParticipantEntity entity);

    @Override
    ChatParticipantEntity toEntity(ChatParticipant domain);
}