package co.dazma.api.publications.services.usecases.impl;

import co.dazma.api.publications.domain.PublicationOwner;
import co.dazma.api.publications.domain.PublicationOwnerCannotOperateException;
import co.dazma.api.publications.domain.PublicationOwnerNotReadyException;
import co.dazma.api.publications.domain.PublicationOwnerState;
import co.dazma.api.publications.services.output.ports.PublicationOwnerRepositoryPort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class OperatingOwnerResolverTest {

    private PublicationOwnerRepositoryPort ownerRepository;
    private OperatingOwnerResolver resolver;

    @BeforeEach
    void setUp() {
        ownerRepository = mock(PublicationOwnerRepositoryPort.class);
        resolver = new OperatingOwnerResolver(ownerRepository);
    }

    @Test
    void shouldReturnTheInternalIdOfAnActiveOwner() {
        UUID userId = UUID.randomUUID();
        when(ownerRepository.findByExternalId("firebase-123"))
                .thenReturn(Optional.of(new PublicationOwner(userId, "firebase-123", PublicationOwnerState.ACTIVE)));

        assertEquals(userId, resolver.resolve("firebase-123"));
    }

    @Test
    void shouldAnswerNotReadyWhenTheLocalCopyDoesNotHaveTheOwnerYet() {
        when(ownerRepository.findByExternalId("firebase-123")).thenReturn(Optional.empty());

        PublicationOwnerNotReadyException exception =
                assertThrows(PublicationOwnerNotReadyException.class, () -> resolver.resolve("firebase-123"));

        assertEquals("PUBLICATION_OWNER_NOT_READY", exception.code());
    }

    @Test
    void shouldRejectASuspendedOwner() {
        when(ownerRepository.findByExternalId("firebase-123")).thenReturn(Optional.of(
                new PublicationOwner(UUID.randomUUID(), "firebase-123", PublicationOwnerState.SUSPENDED)
        ));

        assertThrows(PublicationOwnerCannotOperateException.class, () -> resolver.resolve("firebase-123"));
    }
}
