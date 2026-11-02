package co.dazma.api.chats.services.usecases.impl;

import co.dazma.api.chats.domain.ChatEmailNotVerifiedException;
import co.dazma.api.chats.domain.ChatOffer;
import co.dazma.api.chats.domain.ChatOfferNotFoundException;
import co.dazma.api.chats.domain.ChatParticipant;
import co.dazma.api.chats.domain.ChatRoom;
import co.dazma.api.chats.domain.ChatRoomOpening;
import co.dazma.api.chats.services.output.ports.ChatOfferRepositoryPort;
import co.dazma.api.chats.services.output.ports.ChatRoomRepositoryPort;
import co.dazma.api.chats.services.usecases.contract.OpenChatRoomUseCase;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Clock;
import java.util.Optional;

@Service
public class OpenChatRoomService implements OpenChatRoomUseCase {

    private static final Logger log = LoggerFactory.getLogger(OpenChatRoomService.class);

    private final OperatingParticipantResolver participants;
    private final ChatOfferRepositoryPort offerRepository;
    private final ChatRoomRepositoryPort roomRepository;
    private final Clock clock;

    public OpenChatRoomService(
            OperatingParticipantResolver participants,
            ChatOfferRepositoryPort offerRepository,
            ChatRoomRepositoryPort roomRepository,
            Clock clock
    ) {
        this.participants = participants;
        this.offerRepository = offerRepository;
        this.roomRepository = roomRepository;
        this.clock = clock;
    }

    @Override
    public ChatRoom execute(ChatRoomOpening opening) {
        if (!opening.emailVerified()) {
            throw new ChatEmailNotVerifiedException();
        }

        ChatParticipant interested = participants.resolve(opening.externalId());
        ChatOffer offer = offerRepository.findByPublicationId(opening.publicationId())
                .orElseThrow(() -> new ChatOfferNotFoundException(opening.publicationId()));

        Optional<ChatRoom> existingRoom = roomRepository.findByPublicationAndInterested(
                offer.publicationId(),
                interested.userId()
        );
        if (existingRoom.isPresent()) {
            ChatRoom room = existingRoom.get();
            log.atInfo()
                    .addKeyValue("event.name", "chat.room.reused")
                    .addKeyValue("chat.room.id", room.getId())
                    .addKeyValue("publication.id", offer.publicationId())
                    .log("Sala de chat existente reutilizada");
            return room;
        }

        ChatRoom room = roomRepository.save(ChatRoom.open(
                offer.publicationId(),
                offer.ownerId(),
                interested.userId(),
                clock.instant()
        ));
        log.atInfo()
                .addKeyValue("event.name", "chat.room.created")
                .addKeyValue("chat.room.id", room.getId())
                .addKeyValue("publication.id", offer.publicationId())
                .log("Sala de chat creada");
        return room;
    }
}
