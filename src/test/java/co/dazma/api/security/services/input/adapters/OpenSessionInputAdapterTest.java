package co.dazma.api.security.services.input.adapters;

import co.dazma.api.security.domain.Session;
import co.dazma.api.security.services.input.adapters.mappers.SessionMapper;
import co.dazma.api.security.services.input.dto.OpenSessionInputDto;
import co.dazma.api.security.services.input.dto.OpenSessionOutputDto;
import co.dazma.api.security.services.usecases.contract.OpenSessionUseCase;
import org.junit.jupiter.api.Test;

import java.time.Duration;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class OpenSessionInputAdapterTest {

    @Test
    void shouldReturnTheSessionAsOutput() {
        OpenSessionUseCase useCase = mock(OpenSessionUseCase.class);
        OpenSessionInputAdapter adapter = new OpenSessionInputAdapter(useCase, new SessionMapper());
        when(useCase.execute("id-token")).thenReturn(new Session("session-token", Duration.ofHours(8)));

        OpenSessionOutputDto session = adapter.execute(new OpenSessionInputDto("id-token"));

        assertEquals(new OpenSessionOutputDto("session-token", Duration.ofHours(8)), session);
    }
}
