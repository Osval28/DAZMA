package co.dazma.api.chats.services.usecases.impl;

import co.dazma.api.chats.domain.models.ChatParticipant;
import co.dazma.api.chats.domain.models.ChatRoom;
import co.dazma.api.chats.domain.exceptions.ChatRoomNotFoundException;
import co.dazma.api.chats.services.output.models.ChatTokenRequest;
import co.dazma.api.chats.services.output.ports.ChatRoomRepositoryPort;
import co.dazma.api.chats.services.output.ports.ChatTokenIssuerPort;
import co.dazma.api.chats.services.usecases.contract.IssueChatTokenUseCase;
import co.dazma.api.chats.services.usecases.models.IssueChatTokenCommand;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class IssueChatTokenService implements IssueChatTokenUseCase {

    private static final Logger log = LoggerFactory.getLogger(IssueChatTokenService.class);

    private final OperatingParticipantResolver participants;
    private final ChatRoomRepositoryPort roomRepository;
    private final ChatTokenIssuerPort tokenIssuer;

    public IssueChatTokenService(
            OperatingParticipantResolver participants,
            ChatRoomRepositoryPort roomRepository,
            ChatTokenIssuerPort tokenIssuer
    ) {
        this.participants = participants;
        this.roomRepository = roomRepository;
        this.tokenIssuer = tokenIssuer;
    }

    @Override
    public ChatTokenRequest execute(IssueChatTokenCommand request) {
        ChatParticipant participant = participants.resolve(request.externalId());
        ChatRoom room = roomRepository.findById(request.roomId())
                .orElseThrow(() -> new ChatRoomNotFoundException(request.roomId()));
        if (!room.hasParticipant(participant.userId())) {
            log.atWarn()
                    .addKeyValue("event.name", "chat.room.access.denied")
                    .addKeyValue("chat.room.id", request.roomId())
                    .log("Acceso a la sala de chat rechazado");
            throw new ChatRoomNotFoundException(request.roomId());
        }

        ChatTokenRequest token = tokenIssuer.issue(room, participant);
        log.atInfo()
                .addKeyValue("event.name", "chat.token.issued")
                .addKeyValue("chat.room.id", request.roomId())
                .log("Token efímero de chat emitido");
        return token;
    }
}
