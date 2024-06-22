package br.com.fiap.order.application.validator;

import static br.com.fiap.order.infrastructure.httpclient.user.fields.UserFields.USER_ID_FIELD;
import static br.com.fiap.order.infrastructure.httpclient.user.messages.UserMessages.USER_NOT_FOUND_WITH_ID_MESSAGE;

import br.com.fiap.order.domain.exception.NoResultException;
import br.com.fiap.order.infrastructure.httpclient.user.UserClient;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.validation.FieldError;

@Component
public class CustomerExistsValidator {

  private final UserClient userClient;

  public CustomerExistsValidator(UserClient userClient) {
    this.userClient = userClient;
  }

  public void validate(String customerId) {
    var customerDtoResponseEntity = userClient.getUserById(customerId);
    if (customerDtoResponseEntity.getStatusCode().equals(HttpStatus.NOT_FOUND)) {
      throw new NoResultException(new FieldError(this.getClass().getSimpleName(), USER_ID_FIELD,
          USER_NOT_FOUND_WITH_ID_MESSAGE.formatted(customerId)));
    }
  }
}
