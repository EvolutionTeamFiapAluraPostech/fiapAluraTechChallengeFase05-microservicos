package br.com.fiap.company.domain.exception;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.springframework.validation.FieldError;

class DuplicatedExceptionTest {

  @Test
  void shouldConstructClass() {
    var duplicatedException = new DuplicatedException(
        new FieldError(this.getClass().getSimpleName(), "field", "defaultMessage"));

    assertThat(duplicatedException).isNotNull();
    assertThat(duplicatedException.getFieldError()).isNotNull();
  }
}
