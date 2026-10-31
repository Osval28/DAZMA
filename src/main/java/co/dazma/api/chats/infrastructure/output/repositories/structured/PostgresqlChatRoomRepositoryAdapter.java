package co.dazma.api.chats.infrastructure.output.repositories.structured;

import co.dazma.api.chats.domain.ChatRoom;
import co.dazma.api.chats.infrastructure.output.repositories.structured.entities.ChatRoomEntity;
import co.dazma.api.chats.infrastructure.output.repositories.structured.jpa.entities.ChatRoomEntityJPA;
import co.dazma.api.chats.infrastructure.output.repositories.structured.jpa.repositories.SpringDataChatRoomRepository;
import co.dazma.api.chats.services.output.ports.ChatRoomRepositoryPort;
import co.dazma.api.shared.infrastructure.DomainMapper;
import co.dazma.api.shared.infrastructure.JpaMapper;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public class PostgresqlChatRoomRepositoryAdapter implements ChatRoomRepositoryPort {

    private final SpringDataChatRoomRepository springDataChatRoomRepository;
    private final DomainMapper<ChatRoomEntity, ChatRoom> domainMapper;
    private final JpaMapper<ChatRoomEntityJPA, ChatRoomEntity> jpaMapper;

    public PostgresqlChatRoomRepositoryAdapter(
            SpringDataChatRoomRepository springDataChatRoomRepository,
            DomainMapper<ChatRoomEntity, ChatRoom> domainMapper,
            JpaMapper<ChatRoomEntityJPA, ChatRoomEntity> jpaMapper
    ) {
        this.springDataChatRoomRepository = springDataChatRoomRepository;
        this.domainMapper = domainMapper;
        this.jpaMapper = jpaMapper;
    }

    @Override
    public ChatRoom save(ChatRoom chatRoom) {
        ChatRoomEntityJPA savedRoom = springDataChatRoomRepository.save(
                jpaMapper.toJpaEntity(domainMapper.toEntity(chatRoom))
        );
        return toDomain(savedRoom);
    }

    @Override
    public Optional<ChatRoom> findByPublicationAndInterested(UUID publicationId, UUID interestedId) {
        return springDataChatRoomRepository
                .findByPublicationIdAndInterestedId(publicationId, interestedId)
                .map(this::toDomain);
    }

    @Override
    public List<ChatRoom> findByParticipant(UUID userId) {
        return springDataChatRoomRepository
                .findByOwnerIdOrInterestedIdOrderByCreatedAtDesc(userId, userId)
                .stream()
                .map(this::toDomain)
                .toList();
    }

    private ChatRoom toDomain(ChatRoomEntityJPA jpaEntity) {
        return domainMapper.toDomain(jpaMapper.toEntity(jpaEntity));
    }
}