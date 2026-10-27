package co.dazma.api.notifications.services.input.adapters;

import co.dazma.api.notifications.domain.Recipient;
import co.dazma.api.notifications.services.input.adapters.mappers.RecipientMapper;
import co.dazma.api.notifications.services.input.dto.RegisterRecipientInputDto;
import co.dazma.api.notifications.services.usecases.contract.RegisterRecipientUseCase;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

class RegisterRecipientInputAdapterTest {

    @Test
    void shouldDelegateTheRecipientAsDomainToTheUseCase() {
        RegisterRecipientUseCase useCase = mock(RegisterRecipientUseCase.class);
        RegisterRecipientInputAdapter adapter = new RegisterRecipientInputAdapter(useCase, new RecipientMapper());
        UUID userId = UUID.randomUUID();

        adapter.execute(new RegisterRecipientInputDto(userId, "ana@dazma.store"));

        verify(useCase).execute(new Recipient(userId, "ana@dazma.store"));
    }
}
