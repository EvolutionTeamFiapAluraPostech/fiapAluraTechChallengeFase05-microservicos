package br.com.users;

import static br.com.users.shared.archunit.ApiMethodHasTestArchCondition.haveEquivalentApiMethodTestClass;
import static br.com.users.shared.archunit.ClassHasTestArchCondition.haveEquivalentTestClass;
import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes;
import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.methods;
import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;
import static com.tngtech.archunit.library.Architectures.layeredArchitecture;

import br.com.users.shared.annotation.IntegrationTest;
import com.tngtech.archunit.core.domain.JavaClasses;
import com.tngtech.archunit.core.importer.ClassFileImporter;
import com.tngtech.archunit.core.importer.ImportOption;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.transaction.annotation.Transactional;

class ArchUnitTest {

  private static JavaClasses mainPackages;
  private static JavaClasses testPackages;

  @BeforeAll
  static void init() {
    mainPackages = new ClassFileImporter().withImportOption(new ImportOption.DoNotIncludeTests())
        .importPackages(ArchUnitTest.class.getPackage().getName());
    testPackages = new ClassFileImporter().withImportOption(new ImportOption.OnlyIncludeTests())
        .importPackages(ArchUnitTest.class.getPackage().getName());
  }

  @Test
  void shouldRespectTheLayers() {
    layeredArchitecture().consideringAllDependencies()
        .layer("Presentation").definedBy("..presentation..")
        .layer("Application").definedBy("..application..")
        .layer("Domain").definedBy("..domain..")
        .layer("Infrastructure").definedBy("..infrastructure..")
        .whereLayer("Presentation").mayOnlyBeAccessedByLayers("Infrastructure")
        .whereLayer("Application").mayOnlyBeAccessedByLayers("Infrastructure", "Presentation")
        .whereLayer("Domain")
        .mayOnlyBeAccessedByLayers("Presentation", "Application", "Infrastructure")
        .check(mainPackages);
  }

  @Test
  void shouldNotExistClassesThatArentUseCases() {
    classes().that().resideInAPackage("..application..usecase..")
        .should().haveSimpleNameEndingWith("UseCase")
        .check(mainPackages);
  }

  @Test
  void shouldNotImportRepositoryFromUseCase() {
    noClasses().that().resideInAPackage("..application..usecase..")
        .should().dependOnClassesThat().haveSimpleNameEndingWith("Repository")
        .check(mainPackages);
  }

  @Test
  void shouldNotImportJakartaTransactionTransactionalAnnotation() {
    noClasses().should().dependOnClassesThat()
        .haveFullyQualifiedName(jakarta.transaction.Transactional.class.getName())
        .check(mainPackages);
  }

  @Test
  void shouldBePackageAccessibleAllTestClasses() {
    classes().that().haveSimpleNameEndingWith("Test")
        .and().areNotAnnotations()
        .should().bePackagePrivate()
        .check(testPackages);
  }

  @Test
  void shouldBePackageAccessibleAllTestMethods() {
    methods().that().areDeclaredInClassesThat().haveSimpleNameEndingWith("Test")
        .should().notBePublic()
        .check(testPackages);
  }

  @Test
  void shouldNotUseInputOrOutputDtosInUseCases() {
    noClasses().that().haveSimpleNameEndingWith("UseCase")
        .should().dependOnClassesThat().haveSimpleNameEndingWith("Input")
        .orShould().dependOnClassesThat().haveSimpleNameEndingWith("Output")
        .check(mainPackages);
  }

  @Test
  void shouldIntegrationTestClassesHaveSimpleNameEndingWithApiTest() {
    classes().that().areAnnotatedWith(IntegrationTest.class)
        .should().haveSimpleNameEndingWith("ApiTest")
        .orShould().haveNameNotMatching("DatabaseTest")
        .orShould().haveNameNotMatching("DigitalParkingApplicationTest")
        .check(testPackages);
  }

  @Test
  void shouldUseCaseHaveTransactionalAnnotationInExecuteMethodWhenUseCaseChangeData() {
    methods().that().areDeclaredInClassesThat().haveSimpleNameEndingWith("UseCase")
        .and().areDeclaredInClassesThat().haveSimpleNameNotStartingWith("Get")
        .and().areDeclaredInClassesThat().haveSimpleNameNotStartingWith("Check")
        .and().haveName("execute")
        .should().beAnnotatedWith(Transactional.class)
        .check(mainPackages);
  }

  @Test
  void shouldNotUseCaseHaveTransactionalAnnotationInExecuteMethodWhenUseCaseDoesNotChangeData() {
    methods().that().areDeclaredInClassesThat().haveNameMatching("^.*Get[a-zA-z0-9_]*UseCase$")
        .or().areDeclaredInClassesThat().haveNameMatching("^.*Check[a-zA-z0-9_]*UseCase$")
        .and().haveName("execute")
        .should().notBeAnnotatedWith(Transactional.class)
        .check(mainPackages);
  }

  @Test
  void shouldUseCasesHaveEquivalentTestClass() {
    classes().that().haveSimpleNameEndingWith("UseCase")
        .should(haveEquivalentTestClass(testPackages))
        .check(mainPackages);
  }

  @Test
  void shouldServicesHaveEquivalentTestClass() {
    classes().that().haveSimpleNameEndingWith("Service")
        .should(haveEquivalentTestClass(testPackages))
        .check(mainPackages);
  }

  @Test
  void shouldApiMethodsHaveEquivalentTestClass() {
    classes().that().haveSimpleNameEndingWith("Api")
        .and().resideInAPackage("..presentation.api..")
        .should(haveEquivalentApiMethodTestClass(testPackages))
        .check(mainPackages);
  }
}
