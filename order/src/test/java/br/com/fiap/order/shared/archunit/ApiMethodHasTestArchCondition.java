package br.com.fiap.order.shared.archunit;

import com.tngtech.archunit.core.domain.JavaClass;
import com.tngtech.archunit.core.domain.JavaClasses;
import com.tngtech.archunit.core.domain.JavaMethod;
import com.tngtech.archunit.lang.ConditionEvents;
import com.tngtech.archunit.lang.SimpleConditionEvent;
import java.util.regex.Pattern;

public class ApiMethodHasTestArchCondition<T extends JavaClass> extends TypeHasTestArchCondition<T> {

  private static final String API_VERSION_REGEX = "^.*(?<version>V\\d+)Api$";
  private static final String TEST_CLASS_SUFFIX = "ApiTest";
  private final Pattern versionRegexPattern;

  public ApiMethodHasTestArchCondition(JavaClasses testPackages) {
    super("have associated api test classes", testPackages);
    this.versionRegexPattern = Pattern.compile(API_VERSION_REGEX);
  }

  public static ApiMethodHasTestArchCondition<? super JavaClass> haveEquivalentApiMethodTestClass(
      JavaClasses testPackages) {
    return new ApiMethodHasTestArchCondition<>(testPackages);
  }

  @Override
  public void check(JavaClass clazz, ConditionEvents events) {
    for (JavaMethod method : clazz.getMethods()) {
      if (doesNotHaveEquivalentTestClasses(clazz, method.getName())) {
        events.add(new SimpleConditionEvent(clazz, false,
            "%s does not have a api test class for method: %s".formatted(clazz.getFullName(), method.getName())));
      }
    }
  }

  @Override
  protected String getExpectedTestSuffix(JavaClass clazz) {
    var apiVersion = getApiVersion(clazz.getSimpleName());
    return apiVersion + TEST_CLASS_SUFFIX;
  }

  private String getApiVersion(String className) {
    var versionMatcher = versionRegexPattern.matcher(className);
    if (versionMatcher.matches()) {
      return versionMatcher.group("version");
    } else {
      return "";
    }
  }

}
