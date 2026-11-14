package br.com.projeto.estudioBDD;

import com.tngtech.archunit.core.domain.JavaClasses;
import com.tngtech.archunit.core.importer.ClassFileImporter;
import org.junit.jupiter.api.Test;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;

class ArquiteturaTest {

    @Test
    void controllerNaoEstaChamandoRepositoryTest() {
        JavaClasses importedClasses = new ClassFileImporter()
                .importPackages("br.com.projeto.estudioBDD");

        noClasses()
                .that().resideInAPackage("..controller..")
                .should().dependOnClassesThat().resideInAPackage("..repository..")
                .because("Controladores não devem acessar repositorios")
                .check(importedClasses);
    }

  
}