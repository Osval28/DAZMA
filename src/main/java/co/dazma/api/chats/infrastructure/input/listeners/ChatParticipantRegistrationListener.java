package co.dazma.api.chats.infrastructure.input.listeners;

import co.dazma.api.chats.services.input.dto.RegisterChatParticipantInputDto;
import co.dazma.api.chats.services.input.ports.RegisterChatParticipantInputPort;
import co.dazma.api.users.events.UserRegistered;
import org.springframework.modulith.events.ApplicationModuleListener;
import org.springframework.stereotype.Component;

@Component
public class ChatParticipantRegistrationListener {

    private final RegisterChatParticipantInputPort registerParticipantInputPort;

    public ChatParticipantRegistrationListener(RegisterChatParticipantInputPort registerParticipantInputPort) {
        this.registerParticipantInputPort = registerParticipantInputPort;
    }

    @ApplicationModuleListener
    public void on(UserRegistered event) {
        registerParticipantInputPort.execute(new RegisterChatParticipantInputDto(
                event.userId(),
                event.externalId(),
                event.state()
        ));
    }
}
