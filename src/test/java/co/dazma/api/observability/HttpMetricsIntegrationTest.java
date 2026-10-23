package co.dazma.api.observability;

import co.dazma.api.TestcontainersConfiguration;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Import(TestcontainersConfiguration.class)
@ActiveProfiles("test")
class HttpMetricsIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private MeterRegistry meterRegistry;

    @Test
    void shouldRecordMetricWhenHttpRequestIsHandled() throws Exception {
        mockMvc.perform(get("/actuator/health"))
                .andExpect(status().isOk());

        Timer requestTimer = meterRegistry
                .find("http.server.requests")
                .tag("uri", "/actuator/health")
                .timer();

        assertNotNull(requestTimer);
        assertTrue(requestTimer.count() > 0);
    }
}
