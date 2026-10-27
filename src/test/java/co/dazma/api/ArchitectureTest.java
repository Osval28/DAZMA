package co.dazma.api;

import com.tngtech.archunit.core.domain.JavaClass;
import com.tngtech.archunit.core.importer.ImportOption;
import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.lang.ArchRule;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes;
import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;
import static com.tngtech.archunit.library.Architectures.layeredArchitecture;


@AnalyzeClasses(packages = "co.dazma.api", importOptions = ImportOption.DoNotIncludeTests.class)
class ArchitectureTest {

    @ArchTest
    static final ArchRule DependencyRule =
            layeredArchitecture().consideringOnlyDependenciesInLayers()
                    .withOptionalLayers(true)   // many folders are still empty
                    .layer("Domain").definedBy("..domain..")
                    .layer("Services").definedBy("..services..")
                    .layer("Infrastructure").definedBy("..infrastructure..")
                    .whereLayer("Infrastructure").mayNotBeAccessedByAnyLayer()
                    .whereLayer("Services").mayOnlyBeAccessedByLayers("Infrastructure")
                    .whereLayer("Domain").mayOnlyBeAccessedByLayers("Services", "Infrastructure");

    @ArchTest
    static final ArchRule DoNotUseLibrariesInDomain =
            classes().that().resideInAPackage("..domain..")
                    .should().onlyDependOnClassesThat().resideInAnyPackage("java..", "..domain..");

    @ArchTest
    static final ArchRule ServicesDoNotUsePersistenceDirectly =
            noClasses().that().resideInAPackage("..infrastructure.input..")
                    .should().dependOnClassesThat().resideInAnyPackage(
                            "..services.usecases..", "..services.output..", "..infrastructure.output..").allowEmptyShould(true);

    @ArchTest
    static final ArchRule OutputAdaptersDoNotBypassInputPorts =
            noClasses().that().resideInAPackage("..infrastructure.output..")
                    .should().dependOnClassesThat().resideInAPackage("..services.usecases..")
                    .allowEmptyShould(true);

    @ArchTest
    static final ArchRule UseCasesOnlySpeakDomain =
            noClasses().that().resideInAPackage("..services.usecases..")
                    .should().dependOnClassesThat().resideInAPackage("..services.input..")
                    .allowEmptyShould(true);

    @ArchTest
    static final ArchRule InputPortsAreInterfaces =
            classes().that().resideInAPackage("..services.input.ports..")
                    .and().haveSimpleNameEndingWith("InputPort")
                    .should().beInterfaces()
                    .allowEmptyShould(true);

    @ArchTest
    static final ArchRule UseCaseContractsAreInterfaces =
            classes().that().resideInAPackage("..services.usecases.contract..")
                    .should().beInterfaces()
                    .allowEmptyShould(true);

    @ArchTest
    static final ArchRule InputAdaptersImplementInputPorts =
            classes().that().resideInAPackage("..services.input.adapters")
                    .and().haveSimpleNameEndingWith("InputAdapter")
                    .should().implement(JavaClass.Predicates.resideInAPackage("..services.input.ports.."))
                    .allowEmptyShould(true);
}
