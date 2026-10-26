package co.dazma.api.publications.infrastructure.output.parameters;

import co.dazma.api.shared.services.output.ports.ParameterCatalog;
import org.junit.jupiter.api.Test;

import java.time.Duration;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class CatalogPhotoDownloadParametersAdapterTest {

    @Test
    void shouldReadDownloadValidityFromTheCatalog() {
        CatalogPhotoDownloadParametersAdapter adapter =
                new CatalogPhotoDownloadParametersAdapter(catalog(Duration.ofMinutes(10)));

        assertEquals(Duration.ofMinutes(10), adapter.downloadValidity());
    }

    @Test
    void shouldFailAtStartupWhenValidityIsNotPositive() {
        assertThrows(
                IllegalStateException.class,
                () -> new CatalogPhotoDownloadParametersAdapter(catalog(Duration.ZERO))
        );
    }

    private static ParameterCatalog catalog(Duration validity) {
        ParameterCatalog catalog = mock(ParameterCatalog.class);
        when(catalog.getDuration(PublicationParameter.PHOTO_DOWNLOAD_VALIDITY)).thenReturn(validity);
        return catalog;
    }
}
