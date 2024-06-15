package br.com.fiap.company.application.validator;

import static br.com.fiap.company.shared.testdata.CompanyTestData.DEFAULT_COMPANY_CNPJ_DOC_NUMBER;
import static br.com.fiap.company.shared.testdata.CompanyTestData.DEFAULT_COMPANY_CPF_DOC_NUMBER;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import br.com.fiap.company.domain.enums.DocNumberType;
import br.com.fiap.company.domain.exception.ValidatorException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class DocNumberRequiredValidatorTest {

  @Spy
  private DocNumberRequiredValidator docNumberRequiredValidator;

  @ParameterizedTest
  @NullAndEmptySource
  void shouldThrowExceptionWhenEnterAnInvalidDocNumber(String value) {
    assertThatThrownBy(() -> docNumberRequiredValidator.validate(value, null))
        .isInstanceOf(ValidatorException.class);
  }

  @Test
  void shoudValidateWhenEnterAValidCpfDocNumber() {
    assertThatCode(
        () -> docNumberRequiredValidator.validate(DEFAULT_COMPANY_CPF_DOC_NUMBER, DocNumberType.CPF))
        .doesNotThrowAnyException();
  }

  @Test
  void shoudValidateWhenEnterAValidCnpjDocNumber() {
    assertThatCode(
        () -> docNumberRequiredValidator.validate(DEFAULT_COMPANY_CNPJ_DOC_NUMBER, DocNumberType.CNPJ))
        .doesNotThrowAnyException();
  }
}
