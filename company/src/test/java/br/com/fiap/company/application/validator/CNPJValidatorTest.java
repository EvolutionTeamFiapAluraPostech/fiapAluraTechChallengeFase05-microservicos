package br.com.fiap.company.application.validator;

import static br.com.fiap.company.domain.messages.CompanyMessages.CNPJ_INVALID_MESSAGE;
import static br.com.fiap.company.shared.testdata.CompanyTestData.DEFAULT_COMPANY_CNPJ_DOC_NUMBER;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import br.com.fiap.company.shared.util.StringUtil;
import br.com.fiap.company.domain.exception.ValidatorException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class CNPJValidatorTest {

  @Spy
  private CNPJValidator cnpjValidator;

  @Test
  void shouldValidateCnpj() {
    assertThatCode(() -> cnpjValidator.validate(DEFAULT_COMPANY_CNPJ_DOC_NUMBER))
        .doesNotThrowAnyException();
  }

  @ParameterizedTest
  @NullAndEmptySource
  void shouldThrowExceptionWhenCnpjIsNullOrEmpty(String cnpj) {
    assertThatThrownBy(() -> cnpjValidator.validate(cnpj))
        .isInstanceOf(ValidatorException.class)
        .hasMessageContaining(CNPJ_INVALID_MESSAGE.formatted(cnpj));
  }

  @ParameterizedTest
  @ValueSource(strings = {"0", "1", "2", "3", "4", "5", "6", "7", "8", "9"})
  void shouldThrowExceptionWhenCnpjWithSameSequenceNumberIsInvalid(String number) {
    var cnpj = StringUtil.generateStringRepeatCharLength(number, 14);
    assertThatThrownBy(() -> cnpjValidator.validate(cnpj))
        .isInstanceOf(ValidatorException.class)
        .hasMessageContaining(CNPJ_INVALID_MESSAGE.formatted(cnpj));
  }

  @ParameterizedTest
  @ValueSource(strings = {"1234567890123", "12345678901234", "5905927000011O"})
  void shouldThrowExceptionWhenCnpjIsInvalid(String cnpj) {
    assertThatThrownBy(() -> cnpjValidator.validate(cnpj))
        .isInstanceOf(ValidatorException.class)
        .hasMessageContaining(CNPJ_INVALID_MESSAGE.formatted(cnpj));
  }
}
