package br.com.fiap.company.application.validator;

import static br.com.fiap.company.domain.fields.CompanyFields.COMPANY_ID_FIELD;
import static br.com.fiap.company.domain.messages.CompanyMessages.UUID_INVALID_MESSAGE;

import br.com.fiap.company.shared.util.IsUUID;
import br.com.fiap.company.domain.exception.ValidatorException;
import org.springframework.stereotype.Component;
import org.springframework.validation.FieldError;

@Component
public class UuidValidator {

  public void validate(String uuid) {
    if (uuid == null || uuid.isBlank()) {
      throw new ValidatorException(
          new FieldError(this.getClass().getSimpleName(), COMPANY_ID_FIELD,
              UUID_INVALID_MESSAGE.formatted(uuid)));
    }
    if (!IsUUID.isUUID().matches(uuid)) {
      throw new ValidatorException(
          new FieldError(this.getClass().getSimpleName(), COMPANY_ID_FIELD,
              UUID_INVALID_MESSAGE.formatted(uuid)));
    }
  }
}
