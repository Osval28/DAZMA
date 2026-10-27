package co.dazma.api.notifications.services.usecases;

import co.dazma.api.notifications.domain.Recipient;
import co.dazma.api.notifications.services.input.dto.RegisterRecipientInputDto;
import co.dazma.api.notifications.services.input.ports.RegisterRecipientUseCase;
import co.dazma.api.notifications.services.output.ports.RecipientRepositoryPort;
import org.springframework.stereotype.Service;

@Service
public class RegisterRecipientService implements RegisterRecipientUseCase {

    private final RecipientRepositoryPort recipientRepository;

    public RegisterRecipientService(RecipientRepositoryPort recipientRepository) {
        this.recipientRepository = recipientRepository;
    }

    @Override
    public void register(RegisterRecipientInputDto input) {
        recipientRepository.save(new Recipient(input.userId(), input.email()));
    }
}
