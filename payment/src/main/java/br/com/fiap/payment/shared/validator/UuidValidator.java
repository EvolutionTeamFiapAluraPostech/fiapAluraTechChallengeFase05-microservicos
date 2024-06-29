package br.com.fiap.payment.shared.validator;

import static br.com.fiap.payment.shared.fields.SharedFields.UUID;
import static br.com.fiap.payment.shared.messages.SharedMessages.UUID_INVALID;

import br.com.fiap.payment.domain.exception.ValidatorException;
import br.com.fiap.payment.shared.util.IsUUID;
import org.springframework.stereotype.Component;
import org.springframework.validation.FieldError;

@Component
public class UuidValidator {

  public void validate(String uuid) {
    if (uuid == null || uuid.isBlank()) {
      throw new ValidatorException(
          new FieldError(this.getClass().getSimpleName(), UUID, UUID_INVALID.formatted(uuid)));
    }
    if (!IsUUID.isUUID().matches(uuid)) {
      throw new ValidatorException(
          new FieldError(this.getClass().getSimpleName(), UUID, UUID_INVALID.formatted(uuid)));
    }
  }
}
