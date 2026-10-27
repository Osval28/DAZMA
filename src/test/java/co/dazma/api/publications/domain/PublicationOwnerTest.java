package co.dazma.api.publications.domain;

import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class PublicationOwnerTest {

    @Test
    void shouldOperateOnlyWhenActive() {
        assertTrue(new PublicationOwner(UUID.randomUUID(), "firebase-123", PublicationOwnerState.ACTIVE).canOperate());
        assertFalse(new PublicationOwner(UUID.randomUUID(), "firebase-123", PublicationOwnerState.SUSPENDED).canOperate());
    }

    @Test
    void shouldRequireAnExternalId() {
        InvalidPublicationException exception = assertThrows(
                InvalidPublicationException.class,
                () -> new PublicationOwner(UUID.randomUUID(), " ", PublicationOwnerState.ACTIVE)
        );

        assertEquals("PUBLICATION_OWNER_EXTERNAL_ID_REQUIRED", exception.code());
    }
}
