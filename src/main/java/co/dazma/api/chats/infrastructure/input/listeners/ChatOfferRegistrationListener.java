package co.dazma.api.chats.infrastructure.input.listeners;

import co.dazma.api.chats.services.input.dto.RegisterChatOfferInputDto;
import co.dazma.api.chats.services.input.ports.RegisterChatOfferInputPort;
import co.dazma.api.publications.events.PublicationCreated;
import org.springframework.modulith.events.ApplicationModuleListener;
import org.springframework.stereotype.Component;

@Component
public class ChatOfferRegistrationListener {

    private final RegisterChatOfferInputPort registerOfferInputPort;

    public ChatOfferRegistrationListener(RegisterChatOfferInputPort registerOfferInputPort) {
        this.registerOfferInputPort = registerOfferInputPort;
    }

    @ApplicationModuleListener
    public void on(PublicationCreated event) {
        registerOfferInputPort.execute(new RegisterChatOfferInputDto(
                event.publicationId(),
                event.ownerId()
        ));
    }
}
