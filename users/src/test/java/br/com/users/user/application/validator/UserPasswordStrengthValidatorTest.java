package br.com.users.user.application.validator;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

import br.com.users.user.domain.exception.ValidatorException;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class UserPasswordStrengthValidatorTest {

  @Spy
  private UserPasswordStrengthValidator userPasswordStrengthValidator;

  @ParameterizedTest
  @ValueSource(strings = {"@Bcd1234"})
  void shouldValidatePassword(String password) {
    assertDoesNotThrow(() -> userPasswordStrengthValidator.validate(password));
  }

  @ParameterizedTest
  @ValueSource(strings = {"abcdefghijk", "0ABCDEFGHI", "abcd1234", "Abcd1234", "@#$%^&+=ab1"})
  void shouldThrowExceptionWhenPasswordIsInvalid(String password) {
    assertThrows(ValidatorException.class, () -> userPasswordStrengthValidator.validate(password));
  }
}
