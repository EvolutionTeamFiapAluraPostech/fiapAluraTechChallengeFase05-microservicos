package br.com.fiap.payment.infrastructure.httpclient.validator;

import static br.com.fiap.payment.infrastructure.httpclient.company.messages.CompanyMessages.UUID_INVALID_MESSAGE;

import br.com.fiap.payment.domain.exception.NoResultException;
import br.com.fiap.payment.domain.exception.ValidatorException;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.validation.FieldError;

@Component
public class ResponseEntityValidator {

  public void validate(String id, ResponseEntity<?> responseEntity, String idField,
      String message) {
    if (responseEntity.getStatusCode().is4xxClientError()) {
      int value = responseEntity.getStatusCode().value();
      if (value == 400) {
        throw new ValidatorException(
            new FieldError(this.getClass().getSimpleName(), idField,
                UUID_INVALID_MESSAGE.formatted(id)));
      } else if (value == 404) {
        throw new NoResultException(
            new FieldError(this.getClass().getSimpleName(), idField,
                message.formatted(id)));
      }
    }
  }
}

