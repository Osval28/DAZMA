package co.dazma.api.publications.services.usecases.impl;

import co.dazma.api.publications.domain.PublicationOwner;
import co.dazma.api.publications.domain.PublicationOwnerState;
import co.dazma.api.publications.services.output.ports.PublicationOwnerRepositoryPort;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

class RegisterPublicationOwnerServiceTest {

    @Test
    void shouldStoreTheOwnerInTheLocalCopy() {
        PublicationOwnerRepositoryPort ownerRepository = mock(PublicationOwnerRepositoryPort.class);
        RegisterPublicationOwnerService service = new RegisterPublicationOwnerService(ownerRepository);
        PublicationOwner owner = new PublicationOwner(UUID.randomUUID(), "Ana", "firebase-123", PublicationOwnerState.ACTIVE);

        service.execute(owner);

        verify(ownerRepository).save(owner);
    }
}
