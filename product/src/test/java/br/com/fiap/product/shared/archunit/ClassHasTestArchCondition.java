package br.com.fiap.product.shared.archunit;

import com.tngtech.archunit.core.domain.JavaClass;
import com.tngtech.archunit.core.domain.JavaClasses;
import com.tngtech.archunit.lang.ConditionEvents;
import com.tngtech.archunit.lang.SimpleConditionEvent;

public class ClassHasTestArchCondition<T extends JavaClass> extends TypeHasTestArchCondition<T> {

  private static final String TEST_CLASS_SUFFIX = "Test";

  public ClassHasTestArchCondition(JavaClasses testPackages) {
    super("have associated test classes", testPackages);
  }

  public static ClassHasTestArchCondition<? super JavaClass> haveEquivalentTestClass(JavaClasses testPackages) {
    return new ClassHasTestArchCondition<>(testPackages);
  }

  @Override
  public void check(JavaClass clazz, ConditionEvents events) {
    if (doesNotHaveEquivalentTestClasses(clazz, clazz.getSimpleName())) {
      events.add(new SimpleConditionEvent(clazz, false,
          "%s does not have a test class".formatted(clazz.getFullName())));
    }
  }

  @Override
  protected String getExpectedTestSuffix(JavaClass clazz) {
    return TEST_CLASS_SUFFIX;
  }

}
