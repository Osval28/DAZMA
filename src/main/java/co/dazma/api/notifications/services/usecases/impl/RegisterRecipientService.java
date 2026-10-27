package co.dazma.api.notifications.services.usecases.impl;

import co.dazma.api.notifications.domain.Recipient;
import co.dazma.api.notifications.services.output.ports.RecipientRepositoryPort;
import co.dazma.api.notifications.services.usecases.contract.RegisterRecipientUseCase;
import org.springframework.stereotype.Service;

@Service
public class RegisterRecipientService implements RegisterRecipientUseCase {

    private final RecipientRepositoryPort recipientRepository;

    public RegisterRecipientService(RecipientRepositoryPort recipientRepository) {
        this.recipientRepository = recipientRepository;
    }

    @Override
    public void execute(Recipient recipient) {
        recipientRepository.save(recipient);
    }
}
