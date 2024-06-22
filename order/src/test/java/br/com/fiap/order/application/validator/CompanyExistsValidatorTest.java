package br.com.fiap.order.application.validator;

import static br.com.fiap.order.infrastructure.httpclient.company.fields.CompanyFields.COMPANY_ID_FIELD;
import static br.com.fiap.order.infrastructure.httpclient.company.messages.CompanyMessages.COMPANY_NOT_FOUND_WITH_ID_MESSAGE;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.mockito.Mockito.when;

import br.com.fiap.order.domain.exception.NoResultException;
import br.com.fiap.order.infrastructure.httpclient.company.CompanyHttpClient;
import br.com.fiap.order.infrastructure.httpclient.company.dto.CompanyDto;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;

@ExtendWith(MockitoExtension.class)
class CompanyExistsValidatorTest {

  @Mock
  private CompanyHttpClient companyHttpClient;
  @InjectMocks
  private CompanyExistsValidator companyExistsValidator;

  @Test
  void shouldValidateCompanyIdWhenCompanyExists() {
    var companyId = UUID.randomUUID().toString();
    var companyDtoResponseEntity = new ResponseEntity<CompanyDto>(HttpStatus.OK);
    when(companyHttpClient.getCompanyById(companyId)).thenReturn(companyDtoResponseEntity);

    assertThatCode(() -> companyExistsValidator.validate(companyId)).doesNotThrowAnyException();
  }

  @Test
  void shouldThrowNoResultExceptionWhenCompanyWasNotFoundById() {
    var companyId = UUID.randomUUID().toString();
    when(companyHttpClient.getCompanyById(companyId)).thenThrow(
        new NoResultException(new FieldError(this.getClass().getSimpleName(),
            COMPANY_ID_FIELD, COMPANY_NOT_FOUND_WITH_ID_MESSAGE.formatted(companyId))));

    assertThatCode(() -> companyExistsValidator.validate(companyId))
        .isInstanceOf(NoResultException.class)
        .hasMessage(COMPANY_NOT_FOUND_WITH_ID_MESSAGE.formatted(companyId));
  }
}
