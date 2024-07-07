package br.com.users.user.application.validator;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;

import br.com.users.user.domain.exception.ValidatorException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.password.CompromisedPasswordChecker;
import org.springframework.security.authentication.password.CompromisedPasswordDecision;

@ExtendWith(MockitoExtension.class)
class UserPasswordCompromisedValidatorTest {

  @Mock
  private CompromisedPasswordChecker compromisedPasswordChecker;
  @InjectMocks
  private UserPasswordCompromisedValidator userPasswordCompromisedValidator;

  @ParameterizedTest
  @ValueSource(strings = {"abcd", "senha", "password", "@Bcd1234", "1234"})
  void shouldThrowValidatorExceptionWhenUserPasswordIsCompromised(String password) {
    doThrow(ValidatorException.class).when(compromisedPasswordChecker).check(password);

    assertThatThrownBy(() -> userPasswordCompromisedValidator.validate(password)).isInstanceOf(
        ValidatorException.class);
  }

  @Test
  void shouldValidateWhenUserPasswordIsSafe() {
    var password = "@XptoZyB1138";
    when(compromisedPasswordChecker.check(password)).thenReturn(
        new CompromisedPasswordDecision(false));

    assertThatCode(
        () -> userPasswordCompromisedValidator.validate(password)).doesNotThrowAnyException();
  }
}
