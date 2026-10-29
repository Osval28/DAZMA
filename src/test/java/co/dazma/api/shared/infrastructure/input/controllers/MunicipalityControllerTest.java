package co.dazma.api.shared.infrastructure.input.controllers;

import co.dazma.api.shared.services.input.dto.MunicipalityOutputDto;
import co.dazma.api.shared.services.input.ports.ListMunicipalitiesInputPort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class MunicipalityControllerTest {

    private ListMunicipalitiesInputPort listMunicipalities;
    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        listMunicipalities = mock(ListMunicipalitiesInputPort.class);
        mockMvc = MockMvcBuilders.standaloneSetup(new MunicipalityController(listMunicipalities)).build();
    }

    @Test
    void shouldListMunicipalitiesAsCacheablePublicJson() throws Exception {
        when(listMunicipalities.execute()).thenReturn(List.of(
                new MunicipalityOutputDto("05615", "Rionegro", "Antioquia"),
                new MunicipalityOutputDto("05440", "Marinilla", "Antioquia")
        ));

        mockMvc.perform(get("/shared/api/v1/municipalities"))
                .andExpect(status().isOk())
                .andExpect(header().string("Cache-Control", "max-age=86400, public"))
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].code").value("05615"))
                .andExpect(jsonPath("$[0].name").value("Rionegro"))
                .andExpect(jsonPath("$[0].department").value("Antioquia"))
                .andExpect(jsonPath("$[1].code").value("05440"));
    }

    @Test
    void shouldReturnAnEmptyArrayWhenThereAreNoMunicipalities() throws Exception {
        when(listMunicipalities.execute()).thenReturn(List.of());

        mockMvc.perform(get("/shared/api/v1/municipalities"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0));
    }
}
