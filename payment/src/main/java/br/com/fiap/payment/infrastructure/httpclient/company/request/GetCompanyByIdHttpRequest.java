package br.com.fiap.payment.infrastructure.httpclient.company.request;

import static br.com.fiap.payment.infrastructure.httpclient.company.fields.CompanyFields.COMPANY_ID_FIELD;
import static br.com.fiap.payment.infrastructure.httpclient.company.messages.CompanyMessages.COMPANY_NOT_FOUND_WITH_ID_MESSAGE;

import br.com.fiap.payment.infrastructure.httpclient.company.CompanyHttpClient;
import br.com.fiap.payment.infrastructure.httpclient.company.dto.CompanyDto;
import br.com.fiap.payment.infrastructure.httpclient.validator.ResponseEntityValidator;
import org.springframework.stereotype.Service;

@Service
public class GetCompanyByIdHttpRequest {

  private final CompanyHttpClient companyHttpClient;
  private final ResponseEntityValidator responseEntityValidator;

  public GetCompanyByIdHttpRequest(CompanyHttpClient companyHttpClient,
      ResponseEntityValidator responseEntityValidator) {
    this.companyHttpClient = companyHttpClient;
    this.responseEntityValidator = responseEntityValidator;
  }

  public CompanyDto request(String companyId) {
    var responseEntityCompanyDto = companyHttpClient.getCompanyById(companyId);
    responseEntityValidator.validate(companyId, responseEntityCompanyDto, COMPANY_ID_FIELD,
        COMPANY_NOT_FOUND_WITH_ID_MESSAGE);
    return responseEntityCompanyDto.getBody();
  }
}
