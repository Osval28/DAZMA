package co.dazma.api.chats.infrastructure.output.repositories.structured.mapper;

import co.dazma.api.chats.domain.ChatRoom;
import co.dazma.api.chats.infrastructure.output.repositories.structured.entities.ChatRoomEntity;
import co.dazma.api.shared.infrastructure.DomainMapper;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING,
        unmappedTargetPolicy = ReportingPolicy.ERROR)
public interface ChatRoomMapStructDomainMapper extends DomainMapper<ChatRoomEntity, ChatRoom> {

    @Override
    default ChatRoom toDomain(ChatRoomEntity entity) {
        return ChatRoom.restore(
                entity.id(),
                entity.publicationId(),
                entity.ownerId(),
                entity.interestedId(),
                entity.createdAt()
        );
    }

    @Override
    ChatRoomEntity toEntity(ChatRoom domain);
}