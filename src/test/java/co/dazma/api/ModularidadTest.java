package co.dazma.api;

import org.junit.jupiter.api.Test;
import org.springframework.modulith.core.ApplicationModules;
import org.springframework.modulith.docs.Documenter;

class ModularidadTest {

    private final ApplicationModules modulos = ApplicationModules.of(DazmaApplication.class);

    @Test
    void respetaLosLimitesEntreModulos() {
        modulos.verify();
    }

    @Test
    void generaDocumentacionDeModulos() {
        new Documenter(modulos).writeDocumentation();
    }
}
