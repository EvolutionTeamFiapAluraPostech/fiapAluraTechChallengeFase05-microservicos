package br.com.fiap.payment.infrastructure.httpclient.customer.request;

import static br.com.fiap.payment.infrastructure.httpclient.customer.fields.CustomerFields.CUSTOMER_ID_FIELD;
import static br.com.fiap.payment.infrastructure.httpclient.customer.messages.CustomerMessages.CUSTOMER_NOT_FOUND_WITH_ID_MESSAGE;

import br.com.fiap.payment.infrastructure.httpclient.customer.CustomerHttpClient;
import br.com.fiap.payment.infrastructure.httpclient.customer.dto.CustomerDto;
import br.com.fiap.payment.infrastructure.httpclient.validator.ResponseEntityValidator;
import org.springframework.stereotype.Service;

@Service
public class GetCustomerByIdRequest {

  private final CustomerHttpClient customerHttpClient;
  private final ResponseEntityValidator responseEntityValidator;

  public GetCustomerByIdRequest(CustomerHttpClient customerHttpClient,
      ResponseEntityValidator responseEntityValidator) {
    this.customerHttpClient = customerHttpClient;
    this.responseEntityValidator = responseEntityValidator;
  }

  public CustomerDto request(String id) {
    var responseEntityDto = customerHttpClient.getCustomerById(id);
    responseEntityValidator.validate(id, responseEntityDto, CUSTOMER_ID_FIELD,
        CUSTOMER_NOT_FOUND_WITH_ID_MESSAGE);
    return responseEntityDto.getBody();
  }
}
