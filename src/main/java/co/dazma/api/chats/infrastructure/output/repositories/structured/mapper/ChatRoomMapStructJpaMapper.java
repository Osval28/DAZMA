package co.dazma.api.chats.infrastructure.output.repositories.structured.mapper;

import co.dazma.api.chats.infrastructure.output.repositories.structured.entities.ChatRoomEntity;
import co.dazma.api.chats.infrastructure.output.repositories.structured.jpa.entities.ChatRoomEntityJPA;
import co.dazma.api.shared.infrastructure.JpaMapper;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING,
        unmappedTargetPolicy = ReportingPolicy.ERROR)
public interface ChatRoomMapStructJpaMapper extends JpaMapper<ChatRoomEntityJPA, ChatRoomEntity> {

    @Override
    ChatRoomEntityJPA toJpaEntity(ChatRoomEntity entity);

    @Override
    ChatRoomEntity toEntity(ChatRoomEntityJPA jpaEntity);
}