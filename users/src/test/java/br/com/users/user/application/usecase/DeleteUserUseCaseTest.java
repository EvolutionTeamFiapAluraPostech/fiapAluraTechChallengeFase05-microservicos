package br.com.users.user.application.usecase;

import static br.com.users.shared.testData.user.UserTestData.createUser;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import br.com.users.user.domain.exception.NoResultException;
import br.com.users.user.domain.exception.ValidatorException;
import br.com.users.user.domain.validator.UuidValidator;
import br.com.users.user.domain.service.UserService;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class DeleteUserUseCaseTest {

  @Mock
  private UserService userService;
  @Mock
  private UuidValidator uuidValidator;
  @InjectMocks
  private DeleteUserUseCase deleteUserUseCase;

  @Test
  void shouldDeleteAnUser() {
    var user = createUser();
    when(userService.findById(user.getId())).thenReturn(Optional.of(user));

    assertDoesNotThrow(() -> deleteUserUseCase.execute(user.getId().toString()));

    verify(userService).save(user);
  }

  @Test
  void shouldThrowExceptionWhenDeleteAnUserWasNotFound() {
    var user = createUser();
    when(userService.findById(user.getId())).thenReturn(Optional.empty());

    assertThrows(NoResultException.class, () -> deleteUserUseCase.execute(user.getId().toString()));

    verify(userService, never()).save(user);
  }

  @Test
  void shouldThrowExceptionWhenDeleteAnUserUuidIsInvalid() {
    var userUuid = "aaa";
    doThrow(ValidatorException.class).when(uuidValidator).validate(userUuid);

    assertThrows(ValidatorException.class, () -> deleteUserUseCase.execute(userUuid));

    verify(userService, never()).findById(any());
    verify(userService, never()).save(any());
  }
}
