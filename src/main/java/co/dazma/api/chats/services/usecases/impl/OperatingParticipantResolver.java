package co.dazma.api.chats.services.usecases.impl;

import co.dazma.api.chats.domain.ChatParticipant;
import co.dazma.api.chats.domain.ChatParticipantCannotOperateException;
import co.dazma.api.chats.domain.ChatParticipantNotReadyException;
import co.dazma.api.chats.services.output.ports.ChatParticipantRepositoryPort;
import org.springframework.stereotype.Component;

@Component
public class OperatingParticipantResolver {

    private final ChatParticipantRepositoryPort participantRepository;

    public OperatingParticipantResolver(ChatParticipantRepositoryPort participantRepository) {
        this.participantRepository = participantRepository;
    }

    public ChatParticipant resolve(String externalId) {
        ChatParticipant participant = participantRepository.findByExternalId(externalId)
                .orElseThrow(ChatParticipantNotReadyException::new);
        if (!participant.canOperate()) {
            throw new ChatParticipantCannotOperateException();
        }
        return participant;
    }
}
