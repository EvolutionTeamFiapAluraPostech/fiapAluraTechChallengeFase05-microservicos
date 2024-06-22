package br.com.fiap.order.application.validator;

import static br.com.fiap.order.infrastructure.httpclient.company.fields.CompanyFields.COMPANY_ID_FIELD;
import static br.com.fiap.order.infrastructure.httpclient.company.messages.CompanyMessages.COMPANY_NOT_FOUND_WITH_ID_MESSAGE;

import br.com.fiap.order.domain.exception.NoResultException;
import br.com.fiap.order.infrastructure.httpclient.company.CompanyHttpClient;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.validation.FieldError;

@Component
public class CustomerExistsValidator {

  private final CompanyHttpClient companyHttpClient;

  public CustomerExistsValidator(CompanyHttpClient companyHttpClient) {
    this.companyHttpClient = companyHttpClient;
  }

  public void validate(String companyId) {
    var companyDtoResponseEntity = companyHttpClient.getCompanyById(companyId);
    if (companyDtoResponseEntity.getStatusCode().equals(HttpStatus.NOT_FOUND)) {
      throw new NoResultException(new FieldError(this.getClass().getSimpleName(), COMPANY_ID_FIELD,
          COMPANY_NOT_FOUND_WITH_ID_MESSAGE.formatted(companyId)));
    }
  }
}
