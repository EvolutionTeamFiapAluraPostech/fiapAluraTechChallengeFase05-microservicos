package br.com.users.user.application.usecase;

import static br.com.users.shared.testData.user.UserTestData.DEFAULT_USER_EMAIL;
import static br.com.users.shared.testData.user.UserTestData.DEFAULT_USER_NAME;
import static br.com.users.shared.testData.user.UserTestData.DEFAULT_USER_PASSWORD;
import static br.com.users.shared.testData.user.UserTestData.createUser;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import br.com.users.user.domain.validator.UuidValidator;
import br.com.users.user.application.validator.UserCpfAlreadyRegisteredInOtherUserValidator;
import br.com.users.user.application.validator.UserEmailAlreadyRegisteredInOtherUserValidator;
import br.com.users.user.domain.entity.User;
import br.com.users.user.domain.service.UserService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class UpdateUserUseCaseTest {

  @Mock
  private UserService userService;
  @Mock
  private UuidValidator uuidValidator;
  @Mock
  private UserEmailAlreadyRegisteredInOtherUserValidator userEmailAlreadyRegisteredInOtherUserValidator;
  @Mock
  private UserCpfAlreadyRegisteredInOtherUserValidator userCpfAlreadyRegisteredInOtherUserValidator;
  @InjectMocks
  private UpdateUserUseCase updateUserUseCase;

  @Test
  void shouldUpdateUser() {
    var userFound = createUser();
    var userToUpdate = User.builder()
        .id(userFound.getId())
        .name(DEFAULT_USER_NAME)
        .email(DEFAULT_USER_EMAIL)
        .password(DEFAULT_USER_PASSWORD)
        .build();
    when(userService.findUserByIdRequired(userFound.getId())).thenReturn(userFound);
    when(userService.save(any())).thenReturn(userToUpdate);

    var userUpdated = updateUserUseCase.execute(userFound.getId().toString(), userToUpdate);

    assertThat(userUpdated).isNotNull();
    assertThat(userUpdated).usingRecursiveComparison().isEqualTo(userToUpdate);
    verify(uuidValidator).validate(userToUpdate.getId().toString());
    verify(userEmailAlreadyRegisteredInOtherUserValidator)
        .validate(userToUpdate.getId().toString(), userToUpdate.getEmail());
    verify(userCpfAlreadyRegisteredInOtherUserValidator)
        .validate(userToUpdate.getId().toString(), userToUpdate.getDocNumber());
  }
}
