package br.com.users.user.application.usecase;

import static br.com.users.shared.testData.user.UserTestData.ALTERNATIVE_USER_CPF;
import static br.com.users.shared.testData.user.UserTestData.createUser;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

import br.com.users.user.domain.exception.NoResultException;
import br.com.users.user.domain.messages.UserMessages;
import br.com.users.user.domain.service.UserService;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class GetUserByCpfUseCaseTest {

  @Mock
  private UserService userService;
  @InjectMocks
  private GetUserByCpfUseCase getUserByCpfUseCase;

  @Test
  void shouldFindUserByCpfWhenUserExists() {
    var user = createUser();
    when(userService.findByCpf(user.getDocNumber())).thenReturn(Optional.of(user));

    var userFound = getUserByCpfUseCase.execute(user.getDocNumber());

    assertThat(userFound).isNotNull();
    assertThat(userFound.getDocNumber()).isEqualTo(user.getDocNumber());
  }

  @Test
  void shouldThrowExceptionWhenUserDoesNotExist() {
    when(userService.findByCpf(ALTERNATIVE_USER_CPF)).thenReturn(Optional.empty());

    assertThatThrownBy(() -> getUserByCpfUseCase.execute(ALTERNATIVE_USER_CPF))
        .isInstanceOf(NoResultException.class)
        .hasMessageContaining(UserMessages.USER_CPF_NOT_FOUND.formatted(ALTERNATIVE_USER_CPF));
  }

}