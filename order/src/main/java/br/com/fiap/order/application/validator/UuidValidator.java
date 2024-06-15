package br.com.fiap.order.application.validator;

import br.com.fiap.order.domain.exception.ValidatorException;
import br.com.fiap.order.shared.util.IsUUID;
import org.springframework.stereotype.Component;
import org.springframework.validation.FieldError;

@Component
public class UuidValidator {

  public static final String UUID_FIELD = "uuid";
  public static final String UUID_WITH_INVALID_UUID_MESSAGE = "Order with invalid UUID. ID %s";

  public void validate(String uuid) {
    if (uuid == null || uuid.isBlank()) {
      throw new ValidatorException(
          new FieldError(this.getClass().getSimpleName(), UUID_FIELD,
              UUID_WITH_INVALID_UUID_MESSAGE.formatted(uuid)));
    }
    if (!IsUUID.isUUID().matches(uuid)) {
      throw new ValidatorException(
          new FieldError(this.getClass().getSimpleName(), UUID_FIELD,
              UUID_WITH_INVALID_UUID_MESSAGE.formatted(uuid)));
    }
  }
}
