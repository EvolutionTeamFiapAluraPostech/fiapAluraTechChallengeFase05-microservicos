package br.com.users.user.application.validator;

import static br.com.users.shared.testData.user.UserTestData.DEFAULT_USER_UUID_FROM_STRING;
import static br.com.users.shared.testData.user.UserTestData.createUser;
import static org.junit.jupiter.api.Assertions.*;
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
class UserCpfAlreadyRegisteredInOtherUserValidatorTest {

  @Mock
  private UserService userService;
  @InjectMocks
  private UserCpfAlreadyRegisteredInOtherUserValidator userCpfAlreadyRegisteredInOtherUserValidator;

  @Test
  void shouldValidateWhenUserCpfDoesNotExistInOtherUser() {
    var user = createUser();
    when(userService.findByCpf(user.getDocNumber())).thenReturn(Optional.empty());

    assertDoesNotThrow(() -> userCpfAlreadyRegisteredInOtherUserValidator.validate(
        user.getId().toString(), user.getDocNumber()));
  }

  @Test
  void shouldThrowExceptionWhenUserCpfExistsInOtherUser() {
    var user = createUser();
    when(userService.findByCpf(user.getDocNumber())).thenReturn(Optional.of(user));

    assertThrows(DuplicatedException.class,
        () -> userCpfAlreadyRegisteredInOtherUserValidator.validate(DEFAULT_USER_UUID_FROM_STRING,
            user.getDocNumber()));
  }

}