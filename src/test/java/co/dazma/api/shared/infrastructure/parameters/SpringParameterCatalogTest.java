package co.dazma.api.shared.infrastructure.parameters;

import co.dazma.api.shared.services.output.ports.ParameterKey;
import org.junit.jupiter.api.Test;
import org.springframework.boot.convert.ApplicationConversionService;
import org.springframework.mock.env.MockEnvironment;

import java.time.Duration;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class SpringParameterCatalogTest {

    private static final ParameterKey TYPES = () -> "test.types";
    private static final ParameterKey SIZE = () -> "test.size";
    private static final ParameterKey VALIDITY = () -> "test.validity";
    private static final ParameterKey MISSING = () -> "test.missing";

    private final SpringParameterCatalog catalog = new SpringParameterCatalog(environment());

    @Test
    void shouldReadTypedParameters() {
        assertEquals(Set.of("image/jpeg", "image/png"), catalog.getTextSet(TYPES));
        assertEquals(5L * 1024 * 1024, catalog.getBytes(SIZE));
        assertEquals(Duration.ofMinutes(5), catalog.getDuration(VALIDITY));
    }

    @Test
    void shouldFailWhenTheParameterDoesNotExist() {
        assertThrows(IllegalStateException.class, () -> catalog.getText(MISSING));
    }

    private static MockEnvironment environment() {
        MockEnvironment environment = new MockEnvironment()
                .withProperty("test.types", "image/jpeg, image/png")
                .withProperty("test.size", "5MB")
                .withProperty("test.validity", "5m");
        environment.setConversionService(new ApplicationConversionService());
        return environment;
    }
}
