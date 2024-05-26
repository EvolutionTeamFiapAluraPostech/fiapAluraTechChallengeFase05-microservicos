package br.com.users.user.domain.messages;

public final class UserMessages {

  public static final String USER_EMAIL_NOT_FOUND = "User not found by email %s.";
  public static final String USER_EMAIL_ALREADY_EXISTS = "User already exists with email %s";
  public static final String USER_CPF_ALREADY_EXISTS = "User already exists with cpf %s";
  public static final String USER_CPF_NOT_FOUND = "User not found by cpf %s";
  public static final String USER_EMAIL_INVALID = "User email is invalid %s.";
  public static final String USER_ID_NOT_FOUND = "User not found by ID %s.";
  public static final String USER_PASSWORD_MUST_HAVE_AT_LEAST_ONE_NUMBER_CHAR = "Password must have at least one number character.";
  public static final String USER_PASSWORD_MUST_HAVE_AT_LEAST_ONE_LOWER_CHAR = "Password must have at least one lower character.";
  public static final String USER_PASSWORD_MUST_HAVE_AT_LEAST_ONE_UPPER_CHAR = "Password must have at least one upper character.";
  public static final String USER_PASSWORD_MUST_HAVE_AT_LEAST_ONE_SPECIAL_CHAR = "Password must have at least one special character @#$%^&+= .";
  public static final String USER_DEFAULT_PAYMENT_METHOD_REQUIRED = "User default payment method required.";
}
