package br.com.users.user.application.usecase;

import static br.com.users.shared.testData.user.UserTestData.createUser;
import static br.com.users.user.domain.messages.UserMessages.USER_ID_NOT_FOUND;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import br.com.users.user.domain.exception.NoResultException;
import br.com.users.user.domain.validator.UuidValidator;
import br.com.users.user.domain.service.UserService;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class GetUserByIdUseCaseTest {

  @Mock
  private UserService userService;
  @Mock
  private UuidValidator uuidValidator;
  @InjectMocks
  private GetUserByIdUseCase getUserByIdUseCase;

  @Test
  void shouldFindUserByIdWhenUserExists() {
    var user = createUser();
    var userUuid = user.getId().toString();
    when(userService.findById(UUID.fromString(userUuid))).thenReturn(Optional.of(user));

    var userFound = getUserByIdUseCase.execute(userUuid);

    assertThat(userFound).isNotNull();
    verify(uuidValidator).validate(userUuid);
  }

  @Test
  void shouldThrowExceptionWhenUserDoesNotExist() {
    var userUuid = UUID.randomUUID();
    when(userService.findById(userUuid)).thenReturn(Optional.empty());

    assertThatThrownBy(() -> getUserByIdUseCase.execute(userUuid.toString()))
        .isInstanceOf(NoResultException.class)
        .hasMessageContaining(USER_ID_NOT_FOUND.formatted(userUuid));

    verify(uuidValidator).validate(userUuid.toString());
  }
}
