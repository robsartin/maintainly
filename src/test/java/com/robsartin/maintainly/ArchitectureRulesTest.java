package com.robsartin.maintainly;

import com.tngtech.archunit.core.domain.JavaClasses;
import com.tngtech.archunit.core.importer.ClassFileImporter;
import com.tngtech.archunit.core.importer.ImportOption;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes;
import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;

@DisplayName("Architecture Rules")
class ArchitectureRulesTest {

    private static final String BASE = "com.robsartin.maintainly";
    private static JavaClasses classes;

    @BeforeAll
    static void importClasses() {
        classes = new ClassFileImporter()
                .withImportOption(ImportOption.Predefined
                        .DO_NOT_INCLUDE_TESTS)
                .importPackages(BASE);
    }

    @Nested
    @DisplayName("Hexagonal Architecture")
    class HexagonalArchitecture {

        @Test
        @DisplayName("domain should not depend on application")
        void domainShouldNotDependOnApplication() {
            noClasses()
                    .that().resideInAPackage("..domain..")
                    .should().dependOnClassesThat()
                    .resideInAPackage("..application..")
                    .check(classes);
        }

        @Test
        @DisplayName("domain should not depend on infrastructure")
        void domainShouldNotDependOnInfrastructure() {
            noClasses()
                    .that().resideInAPackage("..domain..")
                    .should().dependOnClassesThat()
                    .resideInAPackage("..infrastructure..")
                    .check(classes);
        }

        @Test
        @DisplayName("application should not depend on infrastructure")
        void applicationShouldNotDependOnInfrastructure() {
            noClasses()
                    .that().resideInAPackage("..application..")
                    .should().dependOnClassesThat()
                    .resideInAPackage("..infrastructure..")
                    .check(classes);
        }
    }

    @Nested
    @DisplayName("Package Organization")
    class PackageOrganization {

        @Test
        @DisplayName("domain models should reside in domain.model")
        void domainModelsInCorrectPackage() {
            classes()
                    .that().resideInAPackage("..domain.model..")
                    .should().haveSimpleNameNotEndingWith(
                            "Service")
                    .check(classes);
        }

        @Test
        @DisplayName("controllers should reside in application.web")
        void controllersInCorrectPackage() {
            classes()
                    .that().haveSimpleNameEndingWith("Controller")
                    .should().resideInAPackage("..application.web..")
                    .check(classes);
        }

        @Test
        @DisplayName("Spring configurations should reside in infrastructure.config")
        void configsInCorrectPackage() {
            classes()
                    .that().haveSimpleNameEndingWith(
                            "Configuration")
                    .should().resideInAPackage(
                            "..infrastructure.config..")
                    .check(classes);
        }
    }
}
