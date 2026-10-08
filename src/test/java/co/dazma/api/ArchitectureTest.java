package co.dazma.api;

import com.tngtech.archunit.core.importer.ImportOption;
import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.lang.ArchRule;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;

@AnalyzeClasses(packages = "co.dazma.api", importOptions = ImportOption.DoNotIncludeTests.class)
class ArchitectureTest {
    public String getClassName() {
        return this.getClass().getSimpleName();
    }
    @ArchTest
    static final ArchRule dominioNoDependeDeOtrasCapasNiFrameworks =
            noClasses().that().resideInAPackage("..domain..")
                    .should().dependOnClassesThat()
                    .resideInAnyPackage("..services..", "..infrastructure..", "org.springframework..")
                    .allowEmptyShould(true);
}
