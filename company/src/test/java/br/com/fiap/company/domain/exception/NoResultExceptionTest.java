package br.com.fiap.company.domain.exception;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.springframework.validation.FieldError;

class NoResultExceptionTest {

  @Test
  void shouldCreateNoResultExceptionClass() {
    var noResultException = new NoResultException(
        new FieldError(this.getClass().getSimpleName(), "field", "defaultMessage"));

    assertThat(noResultException).isNotNull();
    assertThat(noResultException.getFieldError()).isNotNull();
  }

}