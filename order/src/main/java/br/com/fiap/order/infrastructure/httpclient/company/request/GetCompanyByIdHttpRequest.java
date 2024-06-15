package br.com.fiap.order.infrastructure.httpclient.company.request;

import static br.com.fiap.order.infrastructure.httpclient.company.fields.CompanyFields.COMPANY_ID_FIELD;
import static br.com.fiap.order.infrastructure.httpclient.company.messages.CompanyMessages.COMPANY_NOT_FOUND_WITH_ID_MESSAGE;
import static br.com.fiap.order.infrastructure.httpclient.company.messages.CompanyMessages.UUID_INVALID_MESSAGE;

import br.com.fiap.order.domain.exception.NoResultException;
import br.com.fiap.order.domain.exception.ValidatorException;
import br.com.fiap.order.infrastructure.httpclient.company.CompanyHttpClient;
import br.com.fiap.order.infrastructure.httpclient.company.dto.CompanyDto;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.validation.FieldError;

@Service
public class GetCompanyByIdHttpRequest {

  private final CompanyHttpClient companyHttpClient;

  public GetCompanyByIdHttpRequest(CompanyHttpClient companyHttpClient) {
    this.companyHttpClient = companyHttpClient;
  }

  public CompanyDto execute(String companyId) {
    var responseEntityCompanyDto = companyHttpClient.getCompanyById(companyId);
    validateResponseEntity(companyId, responseEntityCompanyDto);
    return responseEntityCompanyDto.getBody();
  }

  private void validateResponseEntity(String companyId,
      ResponseEntity<CompanyDto> responseEntityCompanyDto) {
    if (responseEntityCompanyDto.getStatusCode().is4xxClientError()) {
      int value = responseEntityCompanyDto.getStatusCode().value();
      if (value == 400) {
        throw new ValidatorException(
            new FieldError(this.getClass().getSimpleName(), COMPANY_ID_FIELD,
                UUID_INVALID_MESSAGE.formatted(companyId)));
      } else if (value == 404) {
        throw new NoResultException(
            new FieldError(this.getClass().getSimpleName(), COMPANY_ID_FIELD,
                COMPANY_NOT_FOUND_WITH_ID_MESSAGE.formatted(companyId)));
      }
    }
  }
}
