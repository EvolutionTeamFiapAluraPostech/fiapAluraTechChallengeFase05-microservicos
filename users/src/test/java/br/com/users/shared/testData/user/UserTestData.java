package br.com.users.shared.testData.user;

import static br.com.users.user.domain.enums.DocNumberType.CPF;

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
  public static final String DEFAULT_USER_PASSWORD = "@XptoZyB1138";

  public static User createUser() {
    var uuid = UUID.randomUUID();
    return User.builder()
        .id(uuid)
        .email(ALTERNATIVE_USER_EMAIL)
        .name(ALTERNATIVE_USER_NAME)
        .docNumberType(CPF)
        .docNumber(ALTERNATIVE_USER_CPF)
        .password(DEFAULT_USER_PASSWORD)
        .build();
  }

  public static User createNewUser() {
    return User.builder()
        .email(DEFAULT_USER_EMAIL)
        .name(DEFAULT_USER_NAME)
        .docNumberType(CPF)
        .docNumber(DEFAULT_USER_CPF)
        .password(DEFAULT_USER_PASSWORD)
        .build();
  }
}
