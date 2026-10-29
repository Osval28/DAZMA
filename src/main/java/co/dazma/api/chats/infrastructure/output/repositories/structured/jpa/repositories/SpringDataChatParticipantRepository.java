package co.dazma.api.chats.infrastructure.output.repositories.structured.jpa.repositories;

import co.dazma.api.chats.infrastructure.output.repositories.structured.jpa.entities.ChatParticipantEntityJPA;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface SpringDataChatParticipantRepository extends JpaRepository<ChatParticipantEntityJPA, UUID> {

    Optional<ChatParticipantEntityJPA> findByExternalId(String externalId);
}