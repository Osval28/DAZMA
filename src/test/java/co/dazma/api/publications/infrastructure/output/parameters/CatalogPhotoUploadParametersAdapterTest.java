package co.dazma.api.publications.infrastructure.output.parameters;

import co.dazma.api.shared.services.output.ports.ParameterCatalog;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class CatalogPhotoUploadParametersAdapterTest {

    @Test
    void shouldReadPhotoUploadParametersFromTheCatalog() {
        CatalogPhotoUploadParametersAdapter adapter =
                new CatalogPhotoUploadParametersAdapter(catalog(Set.of("IMAGE/JPEG"), 5_000_000L, Duration.ofMinutes(5)));

        assertEquals(Set.of("image/jpeg"), adapter.allowedContentTypes());
        assertEquals(5_000_000L, adapter.maximumContentLength());
        assertEquals(Duration.ofMinutes(5), adapter.uploadValidity());
    }

    @Test
    void shouldFailAtStartupWhenAParameterIsInvalid() {
        assertThrows(
                IllegalStateException.class,
                () -> new CatalogPhotoUploadParametersAdapter(catalog(Set.of(), 5_000_000L, Duration.ofMinutes(5)))
        );
        assertThrows(
                IllegalStateException.class,
                () -> new CatalogPhotoUploadParametersAdapter(catalog(Set.of("image/png"), 0L, Duration.ofMinutes(5)))
        );
        assertThrows(
                IllegalStateException.class,
                () -> new CatalogPhotoUploadParametersAdapter(catalog(Set.of("image/png"), 5_000_000L, Duration.ZERO))
        );
    }

    private static ParameterCatalog catalog(Set<String> types, long bytes, Duration validity) {
        ParameterCatalog catalog = mock(ParameterCatalog.class);
        when(catalog.getTextSet(PublicationParameter.PHOTO_ALLOWED_CONTENT_TYPES)).thenReturn(types);
        when(catalog.getBytes(PublicationParameter.PHOTO_MAXIMUM_SIZE)).thenReturn(bytes);
        when(catalog.getDuration(PublicationParameter.PHOTO_UPLOAD_VALIDITY)).thenReturn(validity);
        return catalog;
    }
}
