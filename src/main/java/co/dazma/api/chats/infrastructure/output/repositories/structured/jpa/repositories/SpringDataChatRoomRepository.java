package co.dazma.api.chats.infrastructure.output.repositories.structured.jpa.repositories;

import co.dazma.api.chats.infrastructure.output.repositories.structured.jpa.entities.ChatRoomEntityJPA;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface SpringDataChatRoomRepository extends JpaRepository<ChatRoomEntityJPA, UUID> {

    Optional<ChatRoomEntityJPA> findByPublicationIdAndInterestedId(UUID publicationId, UUID interestedId);

    List<ChatRoomEntityJPA> findByOwnerIdOrInterestedIdOrderByCreatedAtDesc(UUID ownerId, UUID interestedId);
}