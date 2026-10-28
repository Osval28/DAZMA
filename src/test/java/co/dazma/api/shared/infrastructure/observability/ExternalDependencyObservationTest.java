package co.dazma.api.shared.infrastructure.observability;

import io.micrometer.core.instrument.Timer;
import io.micrometer.core.instrument.observation.DefaultMeterObservationHandler;
import io.micrometer.core.instrument.simple.SimpleMeterRegistry;
import io.micrometer.observation.Observation;
import io.micrometer.observation.ObservationHandler;
import io.micrometer.observation.ObservationRegistry;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

class ExternalDependencyObservationTest {

    private final List<Observation.Context> stoppedObservations = new ArrayList<>();
    private ExternalDependencyObservation observations;

    @BeforeEach
    void setUp() {
        ObservationRegistry registry = ObservationRegistry.create();
        registry.observationConfig().observationHandler(new ObservationHandler<>() {
            @Override
            public void onStop(Observation.Context context) {
                stoppedObservations.add(context);
            }

            @Override
            public boolean supportsContext(Observation.Context context) {
                return true;
            }
        });
        observations = new ExternalDependencyObservation(registry);
    }

    @Test
    void shouldRecordDependencyAndOperationForSuccessfulCalls() {
        String result = observations.observe("redis", "get_publication", () -> "cached");

        assertEquals("cached", result);
        Observation.Context context = onlyStoppedObservation();
        assertEquals("redis", context.getLowCardinalityKeyValue("dependency").getValue());
        assertEquals("get_publication", context.getLowCardinalityKeyValue("operation").getValue());
    }

    @Test
    void shouldRecordAndPropagateDependencyFailures() {
        IllegalStateException failure = new IllegalStateException("dependency unavailable");

        IllegalStateException thrown = assertThrows(
                IllegalStateException.class,
                () -> observations.observe("cloudflare_r2", "head_object", () -> {
                    throw failure;
                })
        );

        assertSame(failure, thrown);
        assertSame(failure, onlyStoppedObservation().getError());
    }

    @Test
    void shouldRejectBlankDimensions() {
        assertThrows(
                IllegalArgumentException.class,
                () -> observations.observe(" ", "send_email", () -> "sent")
        );
        assertThrows(
                IllegalArgumentException.class,
                () -> observations.observe("resend", " ", () -> "sent")
        );
    }

    @Test
    void shouldPublishMetricsForEveryInstrumentedDependency() {
        SimpleMeterRegistry meterRegistry = new SimpleMeterRegistry();
        ObservationRegistry observationRegistry = ObservationRegistry.create();
        observationRegistry.observationConfig().observationHandler(
                new DefaultMeterObservationHandler(meterRegistry)
        );
        ExternalDependencyObservation meteredObservations =
                new ExternalDependencyObservation(observationRegistry);

        Map<String, String> dependencies = Map.of(
                "redis", "get_publication",
                "cloudflare_r2", "head_object",
                "firebase", "verify_session",
                "resend", "send_email"
        );
        dependencies.forEach((dependency, operation) ->
                meteredObservations.observe(dependency, operation, () -> "ok")
        );

        dependencies.forEach((dependency, operation) -> {
            Timer timer = meterRegistry.find("dazma.external.request")
                    .tag("dependency", dependency)
                    .tag("operation", operation)
                    .timer();
            assertNotNull(timer, () -> "No se publicó la métrica para " + dependency);
            assertEquals(1, timer.count());
        });
    }

    private Observation.Context onlyStoppedObservation() {
        assertEquals(1, stoppedObservations.size());
        return stoppedObservations.getFirst();
    }
}
