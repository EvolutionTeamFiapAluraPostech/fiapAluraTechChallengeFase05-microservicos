package br.com.fiap.company.application.validator;

import static br.com.fiap.company.domain.messages.CompanyMessages.CPF_INVALID_MESSAGE;
import static br.com.fiap.company.shared.testdata.CompanyTestData.DEFAULT_COMPANY_CPF_DOC_NUMBER;
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
class CPFValidatorTest {

  @Spy
  private CPFValidator cpfValidator;

  @Test
  void validateCpf() {
    assertThatCode(() -> cpfValidator.validate(DEFAULT_COMPANY_CPF_DOC_NUMBER))
        .doesNotThrowAnyException();
  }

  @ParameterizedTest
  @NullAndEmptySource
  void shouldThrowExceptionWhenCpfIsNullOrEmpty(String cpf) {
    assertThatThrownBy(() -> cpfValidator.validate(cpf))
        .isInstanceOf(ValidatorException.class)
        .hasMessageContaining(CPF_INVALID_MESSAGE.formatted(cpf));
  }

  @ParameterizedTest
  @ValueSource(strings = {"0", "1", "2", "3", "4", "5", "6", "7", "8", "9"})
  void shouldThrowExceptionWhenCpfWithSameSequenceNumberIsInvalid(String number) {
    var cpf = StringUtil.generateStringRepeatCharLength(number, 11);
    assertThatThrownBy(() -> cpfValidator.validate(cpf))
        .isInstanceOf(ValidatorException.class)
        .hasMessageContaining(CPF_INVALID_MESSAGE.formatted(cpf));
  }

  @ParameterizedTest
  @ValueSource(strings = {"12345678901", "012345A789", "79828932000187"})
  void shouldThrowExceptionWhenCpfIsInvalid(String cpf) {
    assertThatThrownBy(() -> cpfValidator.validate(cpf))
        .isInstanceOf(ValidatorException.class)
        .hasMessageContaining(CPF_INVALID_MESSAGE.formatted(cpf));
  }
}
