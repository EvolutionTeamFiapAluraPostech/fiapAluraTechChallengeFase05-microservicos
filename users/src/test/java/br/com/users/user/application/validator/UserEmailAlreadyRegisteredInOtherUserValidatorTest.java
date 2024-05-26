package br.com.users.user.application.validator;

import static br.com.users.shared.testData.user.UserTestData.DEFAULT_USER_UUID_FROM_STRING;
import static br.com.users.shared.testData.user.UserTestData.createUser;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

import br.com.users.user.domain.exception.DuplicatedException;
import br.com.users.user.domain.service.UserService;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class UserEmailAlreadyRegisteredInOtherUserValidatorTest {

  @Mock
  private UserService userService;
  @InjectMocks
  private UserEmailAlreadyRegisteredInOtherUserValidator userEmailAlreadyRegisteredInOtherUserValidator;

  @Test
  void shouldValidateWhenUserEmailDoesNotExistInOtherEmail() {
    var user = createUser();
    when(userService.findByEmail(user.getEmail())).thenReturn(Optional.empty());

    assertDoesNotThrow(() -> userEmailAlreadyRegisteredInOtherUserValidator.validate(
        user.getId().toString(), user.getEmail()));
  }

  @Test
  void shouldThrowExceptionWhenUserEmailExistsInOtherEmail() {
    var user = createUser();
    when(userService.findByEmail(user.getEmail())).thenReturn(Optional.of(user));

    assertThrows(DuplicatedException.class,
        () -> userEmailAlreadyRegisteredInOtherUserValidator.validate(DEFAULT_USER_UUID_FROM_STRING,
            user.getEmail()));
  }
}
