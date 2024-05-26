package br.com.users.shared.testData.user;

import br.com.users.user.domain.entity.User;
import java.util.UUID;

public final class UserTestData {

  public static final UUID DEFAULT_USER_UUID = UUID.randomUUID();
  public static final String DEFAULT_USER_UUID_FROM_STRING = DEFAULT_USER_UUID.toString();
  public static final String DEFAULT_USER_NAME = "Morpheus";
  public static final String DEFAULT_USER_EMAIL = "morpheus@matrix.com";
  public static final String DEFAULT_USER_CPF = "11955975094";
  public static final String ALTERNATIVE_USER_NAME = "Neo";
  public static final String ALTERNATIVE_USER_EMAIL = "neo@matrix.com";
  public static final String ALTERNATIVE_USER_CPF = "79693503058";
  public static final String DEFAULT_USER_PASSWORD = "@Bcd1234";

  public static final String USER_TEMPLATE_INPUT = """
      {"name": "%s", "email": "%s", "docNumber": "%s", "password": "%s", "docNumberType": "CPF"}""";

  public static final String USER_INPUT = USER_TEMPLATE_INPUT.formatted(
      DEFAULT_USER_NAME,
      DEFAULT_USER_EMAIL,
      DEFAULT_USER_CPF,
      DEFAULT_USER_PASSWORD);

  public static final String ALTERNATIVE_USER_INPUT = USER_TEMPLATE_INPUT.formatted(
      ALTERNATIVE_USER_NAME,
      ALTERNATIVE_USER_EMAIL,
      ALTERNATIVE_USER_CPF,
      DEFAULT_USER_PASSWORD);

  public static final String USER_TEMPLATE_UPDATE = """
      {"name": "%s", "email": "%s", "docNumber": "%s", "docNumber": "CPF"}""";

  public static final String USER_UPDATE = USER_TEMPLATE_UPDATE.formatted(
      ALTERNATIVE_USER_NAME,
      ALTERNATIVE_USER_EMAIL,
      ALTERNATIVE_USER_CPF);

  public static final String USER_PAYMENT_METHOD_TEMPLATE_UPDATE = """
      {"paymentMethod": "%s"}""";


  public static User createUser() {
    var uuid = UUID.randomUUID();
    return User.builder()
        .id(uuid)
        .email(ALTERNATIVE_USER_EMAIL)
        .name(ALTERNATIVE_USER_NAME)
        .docNumber(ALTERNATIVE_USER_CPF)
        .password(DEFAULT_USER_PASSWORD)
        .build();
  }

  public static User createNewUser() {
    return User.builder()
        .email(DEFAULT_USER_EMAIL)
        .name(DEFAULT_USER_NAME)
        .docNumber(DEFAULT_USER_CPF)
        .password(DEFAULT_USER_PASSWORD)
        .build();
  }
}
