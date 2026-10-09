package co.dazma.api;

import com.tngtech.archunit.core.importer.ImportOption;
import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.lang.ArchRule;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;
import static com.tngtech.archunit.library.GeneralCodingRules.NO_CLASSES_SHOULD_ACCESS_STANDARD_STREAMS;
import static com.tngtech.archunit.library.GeneralCodingRules.NO_CLASSES_SHOULD_THROW_GENERIC_EXCEPTIONS;
import static com.tngtech.archunit.library.GeneralCodingRules.NO_CLASSES_SHOULD_USE_FIELD_INJECTION;
import static com.tngtech.archunit.library.GeneralCodingRules.NO_CLASSES_SHOULD_USE_JAVA_UTIL_LOGGING;

@AnalyzeClasses(packages = "co.dazma.api", importOptions = ImportOption.DoNotIncludeTests.class)
class ArchitectureTest {

    @ArchTest
    static final ArchRule dominioNoDependeDeOtrasCapasNiFrameworks =
            noClasses().that().resideInAPackage("..domain..")
                    .should().dependOnClassesThat()
                    .resideInAnyPackage("..services..", "..infrastructure..", "org.springframework..", "jakarta.persistence..")
                    .allowEmptyShould(true);

    @ArchTest
    static final ArchRule serviciosNoDependenDeInfraestructura =
            noClasses().that().resideInAPackage("..services..")
                    .should().dependOnClassesThat().resideInAPackage("..infrastructure..")
                    .allowEmptyShould(true);

    @ArchTest
    static final ArchRule casosDeUsoNoDependenDeLaEntrada =
            noClasses().that().resideInAPackage("..services.usecases..")
                    .should().dependOnClassesThat().resideInAPackage("..services.input..")
                    .allowEmptyShould(true);

    @ArchTest
    static final ArchRule entradaNoUsaDirectamenteLaSalida =
            noClasses().that().resideInAPackage("..infrastructure.input..")
                    .should().dependOnClassesThat()
                    .resideInAnyPackage("..infrastructure.output..", "..services.output..", "..services.usecases..")
                    .allowEmptyShould(true);

    @ArchTest
    static final ArchRule salidaNoDependeDeLaEntrada =
            noClasses().that().resideInAPackage("..infrastructure.output..")
                    .should().dependOnClassesThat().resideInAPackage("..infrastructure.input..")
                    .allowEmptyShould(true);

    @ArchTest
    static final ArchRule sinInyeccionPorCampo = NO_CLASSES_SHOULD_USE_FIELD_INJECTION;

    @ArchTest
    static final ArchRule sinExcepcionesGenericas = NO_CLASSES_SHOULD_THROW_GENERIC_EXCEPTIONS;

    @ArchTest
    static final ArchRule sinJavaUtilLogging = NO_CLASSES_SHOULD_USE_JAVA_UTIL_LOGGING;

    @ArchTest
    static final ArchRule sinSystemOutNiErr = NO_CLASSES_SHOULD_ACCESS_STANDARD_STREAMS;
}
