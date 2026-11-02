package co.dazma.api.chats.services.usecases.impl;

import co.dazma.api.chats.domain.ChatAccessRequest;
import co.dazma.api.chats.domain.ChatParticipant;
import co.dazma.api.chats.domain.ChatRoom;
import co.dazma.api.chats.domain.ChatRoomNotFoundException;
import co.dazma.api.chats.services.output.models.ChatTokenRequest;
import co.dazma.api.chats.services.output.ports.ChatRoomRepositoryPort;
import co.dazma.api.chats.services.output.ports.ChatTokenIssuerPort;
import co.dazma.api.chats.services.usecases.contract.IssueChatTokenUseCase;
import org.springframework.stereotype.Service;

@Service
public class IssueChatTokenService implements IssueChatTokenUseCase {

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
    public ChatTokenRequest execute(ChatAccessRequest request) {
        ChatParticipant participant = participants.resolve(request.externalId());
        ChatRoom room = roomRepository.findById(request.roomId())
                .filter(candidate -> candidate.hasParticipant(participant.userId()))
                .orElseThrow(() -> new ChatRoomNotFoundException(request.roomId()));

        return tokenIssuer.issue(room, participant);
    }
}
