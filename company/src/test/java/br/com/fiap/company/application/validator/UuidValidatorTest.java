package br.com.fiap.company.application.validator;

import static br.com.fiap.company.domain.messages.CompanyMessages.UUID_INVALID_MESSAGE;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import br.com.fiap.company.domain.exception.ValidatorException;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class UuidValidatorTest {

  @Spy
  private UuidValidator uuidValidator;

  @Test
  void shouldValidateId() {
    assertThatCode(() -> uuidValidator.validate(UUID.randomUUID().toString()))
        .doesNotThrowAnyException();
    ;
  }

  @ParameterizedTest
  @NullAndEmptySource
  @ValueSource(strings = {"1AB"})
  void shouldThrowExceptionWhenIdIsInvalid(String invalidUuid) {
    assertThatThrownBy(() -> uuidValidator.validate(invalidUuid))
        .isInstanceOf(ValidatorException.class)
        .hasMessageContaining(UUID_INVALID_MESSAGE.formatted(invalidUuid));
  }
}
