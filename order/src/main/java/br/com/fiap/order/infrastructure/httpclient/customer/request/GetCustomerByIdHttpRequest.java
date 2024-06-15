package br.com.fiap.order.infrastructure.httpclient.customer.request;

import static br.com.fiap.order.infrastructure.httpclient.customer.fields.CustomerFields.CUSTOMER_ID_FIELD;
import static br.com.fiap.order.infrastructure.httpclient.customer.messages.CustomerMessages.CUSTOMER_NOT_FOUND_WITH_ID_MESSAGE;
import static br.com.fiap.order.infrastructure.httpclient.customer.messages.CustomerMessages.UUID_INVALID_MESSAGE;

import br.com.fiap.order.domain.exception.NoResultException;
import br.com.fiap.order.domain.exception.ValidatorException;
import br.com.fiap.order.infrastructure.httpclient.customer.CustomerHttpClient;
import br.com.fiap.order.infrastructure.httpclient.customer.dto.CustomerDto;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.validation.FieldError;

@Service
public class GetCustomerByIdHttpRequest {

  private final CustomerHttpClient customerHttpClient;

  public GetCustomerByIdHttpRequest(CustomerHttpClient customerHttpClient) {
    this.customerHttpClient = customerHttpClient;
  }

  public CustomerDto execute(String companyId) {
    var responseEntityCustomerDto = customerHttpClient.getCustomerById(companyId);
    validateResponseEntity(companyId, responseEntityCustomerDto);
    return responseEntityCustomerDto.getBody();
  }

  private void validateResponseEntity(String companyId,
      ResponseEntity<CustomerDto> responseEntityCustomerDto) {
    if (responseEntityCustomerDto.getStatusCode().is4xxClientError()) {
      int value = responseEntityCustomerDto.getStatusCode().value();
      if (value == 400) {
        throw new ValidatorException(
            new FieldError(this.getClass().getSimpleName(), CUSTOMER_ID_FIELD,
                UUID_INVALID_MESSAGE.formatted(companyId)));
      } else if (value == 404) {
        throw new NoResultException(
            new FieldError(this.getClass().getSimpleName(), CUSTOMER_ID_FIELD,
                CUSTOMER_NOT_FOUND_WITH_ID_MESSAGE.formatted(companyId)));
      }
    }
  }
}
