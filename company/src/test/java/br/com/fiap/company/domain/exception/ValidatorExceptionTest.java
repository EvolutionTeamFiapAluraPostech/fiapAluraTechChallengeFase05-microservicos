package br.com.fiap.company.domain.exception;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.springframework.validation.FieldError;

class ValidatorExceptionTest {

  @Test
  void shouldCreateValidatorExceptionClass() {
    var validatorException = new ValidatorException(
        new FieldError(this.getClass().getSimpleName(), "dield", "defaultMessage"));

    assertThat(validatorException).isNotNull();
    assertThat(validatorException.getFieldError()).isNotNull();
  }
}
