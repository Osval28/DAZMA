package co.dazma.api.publications.infrastructure.configuration;

import org.junit.jupiter.api.Test;
import org.springframework.util.unit.DataSize;

import java.time.Duration;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class PublicationPhotoUploadPropertiesTest {

    @Test
    void shouldNormalizeAValidUploadPolicy() {
        PublicationPhotoUploadProperties properties =
                new PublicationPhotoUploadProperties(
                        Set.of(" IMAGE/JPEG ", "image/png"),
                        DataSize.ofMegabytes(5),
                        Duration.ofMinutes(5)
                );

        assertEquals(Set.of("image/jpeg", "image/png"), properties.allowedContentTypes());
        assertEquals(DataSize.ofMegabytes(5).toBytes(), properties.maximumContentLength());
        assertEquals(Duration.ofMinutes(5), properties.validity());
    }

    @Test
    void shouldRejectAnInvalidUploadPolicy() {
        assertThrows(
                IllegalArgumentException.class,
                () -> new PublicationPhotoUploadProperties(
                        Set.of(),
                        DataSize.ofMegabytes(5),
                        Duration.ofMinutes(5)
                )
        );
        assertThrows(
                IllegalArgumentException.class,
                () -> new PublicationPhotoUploadProperties(
                        Set.of("image/jpeg"),
                        DataSize.ofBytes(0),
                        Duration.ofMinutes(5)
                )
        );
        assertThrows(
                IllegalArgumentException.class,
                () -> new PublicationPhotoUploadProperties(
                        Set.of("image/jpeg"),
                        DataSize.ofMegabytes(5),
                        Duration.ZERO
                )
        );
    }
}
