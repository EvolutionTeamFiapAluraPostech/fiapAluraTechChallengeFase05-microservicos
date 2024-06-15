package br.com.fiap.company.application.validator;

import static br.com.fiap.company.domain.messages.CompanyMessages.DOCUMENT_NUMBER_ALREADY_EXISTS_MESSAGE;
import static br.com.fiap.company.shared.testdata.CompanyTestData.DEFAULT_COMPANY_CPF_DOC_NUMBER;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

import br.com.fiap.company.domain.exception.ValidatorException;
import br.com.fiap.company.domain.service.CompanyService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class DocNumberExistsValidatorTest {

  @Mock
  private CompanyService companyService;
  @InjectMocks
  private DocNumberExistsValidator docNumberExistsValidator;

  @Test
  void shouldValidateDocNumberWhenItDoesNotExist() {
    when(companyService.isCompanyDocNumberAlreadyExists(DEFAULT_COMPANY_CPF_DOC_NUMBER))
        .thenReturn(Boolean.FALSE);

    assertThatCode(() -> docNumberExistsValidator.validate(DEFAULT_COMPANY_CPF_DOC_NUMBER))
        .doesNotThrowAnyException();
  }

  @Test
  void shouldThrowExceptionWhenDocNumberAlreadyExists() {
    when(companyService.isCompanyDocNumberAlreadyExists(DEFAULT_COMPANY_CPF_DOC_NUMBER))
        .thenReturn(Boolean.TRUE);

    assertThatThrownBy(() -> docNumberExistsValidator.validate(DEFAULT_COMPANY_CPF_DOC_NUMBER))
        .isInstanceOf(ValidatorException.class)
        .hasMessageContaining(DOCUMENT_NUMBER_ALREADY_EXISTS_MESSAGE
            .formatted(DEFAULT_COMPANY_CPF_DOC_NUMBER));
  }
}
