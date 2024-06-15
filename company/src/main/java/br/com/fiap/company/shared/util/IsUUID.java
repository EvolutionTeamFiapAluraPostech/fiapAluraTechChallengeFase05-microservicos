package br.com.fiap.company.shared.util;

import java.util.regex.Pattern;
import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;

public class IsUUID extends TypeSafeMatcher<String> {

  private static final String UUID_REGEX = "^(.{8})-(.{4})-(.{4})-(.{4})-(.{12})$";
  private final Pattern pattern;

  private IsUUID(Pattern pattern) {
    this.pattern = pattern;
  }

  public static Matcher<String> isUUID() {
    return new IsUUID(Pattern.compile(UUID_REGEX));
  }

  @Override
  protected boolean matchesSafely(String uuid) {
    return pattern.matcher(uuid).matches();
  }

  @Override
  public void describeTo(Description description) {
    description.appendText("a string matching the valid UUID");
  }

}
