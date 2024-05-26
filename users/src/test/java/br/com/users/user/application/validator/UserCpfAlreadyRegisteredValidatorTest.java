package br.com.users.user.application.validator;

import static br.com.users.shared.testData.user.UserTestData.createNewUser;
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
class UserCpfAlreadyRegisteredValidatorTest {

  @Mock
  private UserService userService;
  @InjectMocks
  private UserCpfAlreadyRegisteredValidator userCpfAlreadyRegisteredValidator;

  @Test
  void shouldValidateWhenUserCpfDoesNotExist() {
    var user = createNewUser();
    when(userService.findByCpf(user.getDocNumber())).thenReturn(Optional.empty());

    assertDoesNotThrow(() -> userCpfAlreadyRegisteredValidator.validate(user.getDocNumber()));
  }

  @Test
  void shouldThrowExceptionWhenUserCpfAlreadyExists() {
    var user = createUser();
    when(userService.findByCpf(user.getDocNumber())).thenReturn(Optional.of(user));

    assertThrows(DuplicatedException.class,
        () -> userCpfAlreadyRegisteredValidator.validate(user.getDocNumber()));
  }

}