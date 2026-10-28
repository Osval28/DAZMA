package co.dazma.api.publications.services.input.adapters;

import co.dazma.api.publications.domain.PublicationOwner;
import co.dazma.api.publications.domain.PublicationOwnerState;
import co.dazma.api.publications.services.input.adapters.mappers.PublicationOwnerMapper;
import co.dazma.api.publications.services.input.dto.RegisterPublicationOwnerInputDto;
import co.dazma.api.publications.services.usecases.contract.RegisterPublicationOwnerUseCase;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

class RegisterPublicationOwnerInputAdapterTest {

    @Test
    void shouldGiveTheUseCaseTheOwnerAsDomain() {
        RegisterPublicationOwnerUseCase useCase = mock(RegisterPublicationOwnerUseCase.class);
        RegisterPublicationOwnerInputAdapter adapter =
                new RegisterPublicationOwnerInputAdapter(useCase, new PublicationOwnerMapper());
        UUID userId = UUID.randomUUID();

        adapter.execute(new RegisterPublicationOwnerInputDto(userId, "firebase-123", "ACTIVE"));

        verify(useCase).execute(new PublicationOwner(userId, "firebase-123", PublicationOwnerState.ACTIVE));
    }
}
