package co.dazma.api.chats.services.output.ports;

import co.dazma.api.chats.domain.models.ChatParticipant;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ChatParticipantRepositoryPort {

    void save(ChatParticipant participant);

    Optional<ChatParticipant> findByExternalId(String externalId);

    List<ChatParticipant> findAllByUserIds(Collection<UUID> userIds);
}