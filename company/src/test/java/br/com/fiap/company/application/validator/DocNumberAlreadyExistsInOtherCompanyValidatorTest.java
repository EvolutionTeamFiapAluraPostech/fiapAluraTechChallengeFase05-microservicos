package br.com.fiap.company.application.validator;

import static br.com.fiap.company.domain.messages.CompanyMessages.DOCUMENT_NUMBER_ALREADY_EXISTS_IN_OTHER_COMPANY_MESSAGE;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

import br.com.fiap.company.domain.exception.ValidatorException;
import br.com.fiap.company.domain.service.CompanyService;
import br.com.fiap.company.shared.testdata.CompanyTestData;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class DocNumberAlreadyExistsInOtherCompanyValidatorTest {

  @Mock
  private CompanyService companyService;
  @InjectMocks
  private DocNumberAlreadyExistsInOtherCompanyValidator docNumberAlreadyExistsInOtherCompanyValidator;

  @Test
  void shouldValidateDocNumber() {
    var company = CompanyTestData.createCompany();
    when(companyService.findByDocNumber(company.getDocNumber())).thenReturn(
        Optional.of(company));

    assertThatCode(
        () -> docNumberAlreadyExistsInOtherCompanyValidator.validate(company.getDocNumber(),
            company.getId())).doesNotThrowAnyException();
  }

  @Test
  void shouldThrowExceptionWhenDocNumberAlreadyExistsInOtherCompany() {
    var company = CompanyTestData.createCompany();
    var companyId = UUID.randomUUID();
    when(companyService.findByDocNumber(company.getDocNumber())).thenReturn(
        Optional.of(company));

    assertThatThrownBy(
        () -> docNumberAlreadyExistsInOtherCompanyValidator.validate(company.getDocNumber(),
            companyId))
        .isInstanceOf(ValidatorException.class)
        .hasMessage(DOCUMENT_NUMBER_ALREADY_EXISTS_IN_OTHER_COMPANY_MESSAGE.formatted(
            company.getDocNumber()));
  }
}
