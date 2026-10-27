package co.dazma.api.notifications.services.usecases.impl;

import co.dazma.api.notifications.domain.Recipient;
import co.dazma.api.notifications.services.output.ports.RecipientRepositoryPort;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

class RegisterRecipientServiceTest {

    @Test
    void shouldStoreTheRecipientLocalCopy() {
        RecipientRepositoryPort recipientRepository = mock(RecipientRepositoryPort.class);
        RegisterRecipientService service = new RegisterRecipientService(recipientRepository);
        UUID userId = UUID.randomUUID();

        service.execute(new Recipient(userId, "ana@dazma.store"));

        verify(recipientRepository).save(new Recipient(userId, "ana@dazma.store"));
    }
}
