package br.com.fiap.product.shared.archunit;

import com.tngtech.archunit.core.domain.JavaClass;
import com.tngtech.archunit.core.domain.JavaClasses;
import com.tngtech.archunit.lang.ArchCondition;
import org.springframework.util.StringUtils;

public abstract class TypeHasTestArchCondition<T> extends ArchCondition<T> {

  private final JavaClasses testPackages;

  public TypeHasTestArchCondition(String description, JavaClasses testPackages) {
    super(description);
    this.testPackages = testPackages;
  }

  protected abstract String getExpectedTestSuffix(JavaClass clazz);

  protected boolean doesNotHaveEquivalentTestClasses(JavaClass clazz, String testName) {
    return !hasEquivalentTestClasses(clazz, testName);
  }

  protected boolean hasEquivalentTestClasses(JavaClass clazz, String baseTestName) {
    var expectedTestSuffix = getExpectedTestSuffix(clazz);
    var expectedTestClassNamePrefix = StringUtils.capitalize(baseTestName);
    var expectedTestFullNamePrefix = clazz.getPackageName() + "." + expectedTestClassNamePrefix;
    return this.testPackages.contain(expectedTestFullNamePrefix + expectedTestSuffix) ||
        this.testPackages.contain(expectedTestFullNamePrefix + "Microsoft" + expectedTestSuffix) ||
        this.testPackages.contain(expectedTestFullNamePrefix + "Sienge" + expectedTestSuffix) ||
        this.testPackages.contain(expectedTestFullNamePrefix + "SiengeId" + expectedTestSuffix) ||
        this.testPackages.contain(expectedTestFullNamePrefix + "SiengeOrg" + expectedTestSuffix);
  }

}
