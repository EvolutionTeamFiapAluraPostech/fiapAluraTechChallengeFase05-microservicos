package br.com.fiap.product.application.validator;

import static br.com.fiap.product.domain.fields.ProductFields.PRODUCT_ID_FIELD;
import static br.com.fiap.product.domain.messages.ProductMessages.PRODUCT_WITH_INVALID_UUID_MESSAGE;

import br.com.fiap.product.domain.exception.ValidatorException;
import br.com.fiap.product.shared.util.IsUUID;
import org.springframework.stereotype.Component;
import org.springframework.validation.FieldError;

@Component
public class UuidValidator {

  public void validate(String uuid) {
    if (uuid == null || uuid.isBlank()) {
      throw new ValidatorException(
          new FieldError(this.getClass().getSimpleName(), PRODUCT_ID_FIELD,
              PRODUCT_WITH_INVALID_UUID_MESSAGE.formatted(uuid)));
    }
    if (!IsUUID.isUUID().matches(uuid)) {
      throw new ValidatorException(
          new FieldError(this.getClass().getSimpleName(), PRODUCT_ID_FIELD,
              PRODUCT_WITH_INVALID_UUID_MESSAGE.formatted(uuid)));
    }
  }
}
