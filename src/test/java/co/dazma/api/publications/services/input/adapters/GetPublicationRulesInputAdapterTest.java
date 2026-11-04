package co.dazma.api.publications.services.input.adapters;

import co.dazma.api.publications.domain.models.PublicationRules;
import co.dazma.api.publications.services.input.dto.PublicationRulesOutputDto;
import co.dazma.api.publications.services.output.ports.PhotoUploadParametersPort;
import co.dazma.api.publications.services.output.ports.PublicationRulesPort;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class GetPublicationRulesInputAdapterTest {

    @Test
    void shouldExposeCurrentRulesAndPhotoLimitsTogether() {
        PublicationRulesPort rules = mock(PublicationRulesPort.class);
        PhotoUploadParametersPort photos = mock(PhotoUploadParametersPort.class);
        when(rules.current()).thenReturn(new PublicationRules(7, 60, 15));
        when(photos.allowedContentTypes()).thenReturn(Set.of("image/jpeg", "image/png"));
        when(photos.maximumContentLength()).thenReturn(5_000_000L);

        PublicationRulesOutputDto result = new GetPublicationRulesInputAdapter(rules, photos).execute();

        assertEquals(7, result.maximumPhotos());
        assertEquals(60, result.maximumPageSize());
        assertEquals(15, result.defaultPageSize());
        assertEquals(Set.of("image/jpeg", "image/png"), result.allowedPhotoContentTypes());
        assertEquals(5_000_000L, result.maximumPhotoBytes());
    }
}
