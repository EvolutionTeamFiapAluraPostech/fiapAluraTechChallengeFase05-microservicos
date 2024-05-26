package br.com.users.shared.model.entity.validator;

import static br.com.users.shared.testData.user.UserTestData.DEFAULT_USER_EMAIL;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

import br.com.users.user.domain.validator.EmailValidator;
import br.com.users.user.domain.exception.ValidatorException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class EmailValidatorTest {

  @Spy
  private EmailValidator emailValidator;

  @Test
  void shouldValidateEmail() {
    assertDoesNotThrow(() -> emailValidator.validate(DEFAULT_USER_EMAIL));
  }

  @ParameterizedTest
  @ValueSource(strings = {"email.domain.com", " email.domain.com", "@", "1", "email@domain",
      "A@b@c@example.com", "a\"b(c)d,e:f;g<h>i[j\\k]l@example.com", "email @example.com"})
  void shouldThrowExceptionWhenEmailIsInvalid(String email) {
    assertThrows(ValidatorException.class, () -> emailValidator.validate(email));
  }

  @ParameterizedTest
  @NullAndEmptySource
  void shouldDoNothingWhenEmailIsNullOrEmpty(String email) {
    assertDoesNotThrow(() -> emailValidator.validate(email));
  }
}
