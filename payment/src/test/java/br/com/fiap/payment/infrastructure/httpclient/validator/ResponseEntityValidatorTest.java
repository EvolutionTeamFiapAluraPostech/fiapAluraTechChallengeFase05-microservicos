package br.com.fiap.payment.infrastructure.httpclient.validator;

import static br.com.fiap.payment.infrastructure.httpclient.company.fields.CompanyFields.COMPANY_ID_FIELD;
import static br.com.fiap.payment.infrastructure.httpclient.company.messages.CompanyMessages.COMPANY_NOT_FOUND_WITH_ID_MESSAGE;
import static br.com.fiap.payment.shared.testdata.CompanyTestData.createCompanyDto;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import br.com.fiap.payment.domain.exception.NoResultException;
import br.com.fiap.payment.domain.exception.ValidatorException;
import br.com.fiap.payment.infrastructure.httpclient.company.dto.CompanyDto;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

@ExtendWith(MockitoExtension.class)
class ResponseEntityValidatorTest {

  @Spy
  private ResponseEntityValidator responseEntityValidator;

  @Test
  void shouldValidateWhenCompanyWasFoundById() {
    var companyDto = createCompanyDto();
    var responseEntityCompany = new ResponseEntity<CompanyDto>(companyDto, HttpStatus.OK);

    assertThatCode(() -> responseEntityValidator.validate(companyDto.id(), responseEntityCompany,
        COMPANY_ID_FIELD, COMPANY_NOT_FOUND_WITH_ID_MESSAGE)).doesNotThrowAnyException();
  }

  @ParameterizedTest
  @NullAndEmptySource
  @ValueSource(strings = "1aB")
  void shouldThrowValidatorExceptionWhenCompanyHasInvalidId(String id) {
    var companyDto = createCompanyDto();
    var responseEntityCompany = new ResponseEntity<CompanyDto>(companyDto, HttpStatus.BAD_REQUEST);

    assertThatThrownBy(
        () -> responseEntityValidator.validate(id, responseEntityCompany,
            COMPANY_ID_FIELD, COMPANY_NOT_FOUND_WITH_ID_MESSAGE)).isInstanceOf(
        ValidatorException.class);
  }

  @Test
  void shouldThrowValidatorExceptionWhenCompanyHasInvalidId() {
    var id = UUID.randomUUID().toString();
    var companyDto = createCompanyDto();
    var responseEntityCompany = new ResponseEntity<CompanyDto>(companyDto, HttpStatus.NOT_FOUND);

    assertThatThrownBy(
        () -> responseEntityValidator.validate(id, responseEntityCompany,
            COMPANY_ID_FIELD, COMPANY_NOT_FOUND_WITH_ID_MESSAGE)).isInstanceOf(
        NoResultException.class);
  }
}
