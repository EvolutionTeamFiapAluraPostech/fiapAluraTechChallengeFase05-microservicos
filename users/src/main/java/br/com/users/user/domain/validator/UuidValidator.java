package br.com.users.user.domain.validator;

import static br.com.users.shared.messages.SharedMessages.UUID_INVALID;

import br.com.users.user.domain.exception.ValidatorException;
import br.com.users.shared.util.IsUUID;
import org.springframework.stereotype.Component;
import org.springframework.validation.FieldError;

@Component
public class UuidValidator {

  public void validate(String uuid) {
    if (!IsUUID.isUUID().matches(uuid)) {
      throw new ValidatorException(new FieldError(this.getClass().getSimpleName(), "UUID",
          UUID_INVALID.formatted(uuid)));
    }
  }
}
