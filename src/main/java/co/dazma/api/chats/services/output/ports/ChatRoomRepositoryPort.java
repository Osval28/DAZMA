package co.dazma.api.chats.services.output.ports;

import co.dazma.api.chats.domain.ChatRoom;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ChatRoomRepositoryPort {

    ChatRoom save(ChatRoom chatRoom);

    Optional<ChatRoom> findByPublicationAndInterested(UUID publicationId, UUID interestedId);

    List<ChatRoom> findByParticipant(UUID userId);
}