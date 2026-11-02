package co.dazma.api.chats.infrastructure.output.repositories.structured.jpa.repositories;

import co.dazma.api.chats.infrastructure.output.repositories.structured.jpa.entities.ChatOfferEntityJPA;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface SpringDataChatOfferRepository extends JpaRepository<ChatOfferEntityJPA, UUID> {
}
