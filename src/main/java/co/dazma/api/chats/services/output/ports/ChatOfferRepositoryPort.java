package co.dazma.api.chats.services.output.ports;

import co.dazma.api.chats.domain.ChatOffer;

import java.util.Optional;
import java.util.UUID;

public interface ChatOfferRepositoryPort {

    void save(ChatOffer offer);

    Optional<ChatOffer> findByPublicationId(UUID publicationId);
}
