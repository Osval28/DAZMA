package co.dazma.api.chats.services.usecases.impl;

import co.dazma.api.chats.domain.models.ChatParticipant;
import co.dazma.api.chats.services.output.ports.ChatParticipantRepositoryPort;
import co.dazma.api.chats.services.usecases.contract.RegisterChatParticipantUseCase;
import org.springframework.stereotype.Service;

@Service
public class RegisterChatParticipantService implements RegisterChatParticipantUseCase {

    private final ChatParticipantRepositoryPort participantRepository;

    public RegisterChatParticipantService(ChatParticipantRepositoryPort participantRepository) {
        this.participantRepository = participantRepository;
    }

    @Override
    public void execute(ChatParticipant participant) {
        participantRepository.save(participant);
    }
}
