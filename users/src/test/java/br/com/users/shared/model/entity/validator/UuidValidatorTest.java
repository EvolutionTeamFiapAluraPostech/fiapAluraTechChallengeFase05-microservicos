package br.com.users.shared.model.entity.validator;

import static org.junit.jupiter.api.Assertions.*;

import br.com.users.user.domain.validator.UuidValidator;
import br.com.users.user.domain.exception.ValidatorException;
import java.util.UUID;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class UuidValidatorTest {

  @Spy
  private UuidValidator uuidValidator;

  @Test
  void shouldValidateUuid() {
    var uuid = UUID.randomUUID();
    Assertions.assertDoesNotThrow(() -> uuidValidator.validate(uuid.toString()));
  }

  @Test
  void shouldThrowExceptionWhenUuidIsInvalid() {
    var invalidUuid = "abc";
    assertThrows(ValidatorException.class, () -> uuidValidator.validate(invalidUuid));
  }
}
