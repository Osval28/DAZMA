package co.dazma.api.chats.services.output.ports;

import co.dazma.api.chats.domain.ChatParticipant;

import java.util.Optional;

public interface ChatParticipantRepositoryPort {

    void save(ChatParticipant participant);

    Optional<ChatParticipant> findByExternalId(String externalId);
}