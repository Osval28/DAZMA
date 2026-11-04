package co.dazma.api.chats.services.usecases.impl;

import co.dazma.api.chats.domain.models.ChatOffer;
import co.dazma.api.chats.services.output.ports.ChatOfferRepositoryPort;
import co.dazma.api.chats.services.usecases.contract.RegisterChatOfferUseCase;
import org.springframework.stereotype.Service;

@Service
public class RegisterChatOfferService implements RegisterChatOfferUseCase {

    private final ChatOfferRepositoryPort offerRepository;

    public RegisterChatOfferService(ChatOfferRepositoryPort offerRepository) {
        this.offerRepository = offerRepository;
    }

    @Override
    public void execute(ChatOffer offer) {
        offerRepository.save(offer);
    }
}
