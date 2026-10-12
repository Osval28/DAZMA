package co.dazma.api;

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
    static final ArchRule ControllersOnlyTalkToInputPorts =
            noClasses().that().resideInAPackage("..infrastructure.input..")
                    .should().dependOnClassesThat().resideInAnyPackage(
                            "..services.usecases..", "..services.output..", "..infrastructure.output..");





}
