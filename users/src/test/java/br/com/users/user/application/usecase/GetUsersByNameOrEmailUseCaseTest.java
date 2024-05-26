package br.com.users.user.application.usecase;

import static br.com.users.shared.api.PageUtil.PAGE_NUMBER;
import static br.com.users.shared.api.PageUtil.PAGE_SIZE;
import static br.com.users.shared.testData.user.UserTestData.DEFAULT_USER_EMAIL;
import static br.com.users.shared.testData.user.UserTestData.DEFAULT_USER_NAME;
import static br.com.users.shared.testData.user.UserTestData.createUser;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import br.com.users.user.domain.validator.EmailValidator;
import br.com.users.user.domain.entity.User;
import br.com.users.user.domain.service.UserService;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

@ExtendWith(MockitoExtension.class)
class GetUsersByNameOrEmailUseCaseTest {

  @Mock
  private UserService userService;
  @Mock
  private EmailValidator emailValidator;
  @InjectMocks
  private GetUsersByNameOrEmailUseCase getUsersByNameOrEmailUseCase;

  @Test
  void shouldFindUsersByNameOrEmailWhenUserExists() {
    var user = createUser();
    var userName = user.getName();
    var userEmail = user.getEmail();
    var users = List.of(user);
    var pageable = PageRequest.of(PAGE_NUMBER, PAGE_SIZE);
    var size = users.size();
    var page = new PageImpl<>(users, pageable, size);

    when(userService.queryUserByNameLikeIgnoreCaseOrEmail(userName, userEmail, pageable)).thenReturn(page);
    var usersFound = getUsersByNameOrEmailUseCase.execute(userName, userEmail, pageable);

    assertThat(usersFound).isNotNull();
    assertThat(usersFound.getSize()).isEqualTo(PAGE_SIZE);
    assertThat(usersFound.getTotalPages()).isEqualTo(size);
    assertThat(usersFound.getTotalElements()).isEqualTo(size);
    verify(emailValidator).validate(userEmail);
  }

  @Test
  void shouldFindUsersByNameWhenUserExists() {
    var user = createUser();
    var userName = user.getName();
    var userEmail = "";
    var users = List.of(user);
    var pageable = PageRequest.of(PAGE_NUMBER, PAGE_SIZE);
    var size = users.size();
    var page = new PageImpl<>(users, pageable, size);

    when(userService.queryUserByNameLikeIgnoreCaseOrEmail(userName, userEmail, pageable)).thenReturn(page);
    var usersFound = getUsersByNameOrEmailUseCase.execute(userName, userEmail, pageable);

    assertThat(usersFound).isNotNull();
    assertThat(usersFound.getSize()).isEqualTo(PAGE_SIZE);
    assertThat(usersFound.getTotalPages()).isEqualTo(size);
    assertThat(usersFound.getTotalElements()).isEqualTo(size);
    verify(emailValidator).validate(userEmail);
  }

  @Test
  void shouldFindUsersByEmailWhenUserExists() {
    var user = createUser();
    var userName = "";
    var userEmail = user.getEmail();
    var users = List.of(user);
    var pageable = PageRequest.of(PAGE_NUMBER, PAGE_SIZE);
    var size = users.size();
    var page = new PageImpl<>(users, pageable, size);

    when(userService.queryUserByNameLikeIgnoreCaseOrEmail(userName, userEmail, pageable)).thenReturn(page);
    var usersFound = getUsersByNameOrEmailUseCase.execute(userName, userEmail, pageable);

    assertThat(usersFound).isNotNull();
    assertThat(usersFound.getSize()).isEqualTo(PAGE_SIZE);
    assertThat(usersFound.getTotalPages()).isEqualTo(size);
    assertThat(usersFound.getTotalElements()).isEqualTo(size);
    verify(emailValidator).validate(userEmail);
  }

  @Test
  void shouldReturnEmptyWhenUserDoesNotExist() {
    var userName = DEFAULT_USER_NAME;
    var userEmail = DEFAULT_USER_EMAIL;
    var users = new ArrayList<User>();
    var pageable = PageRequest.of(PAGE_NUMBER, PAGE_SIZE);
    var size = 0;
    var page = new PageImpl<>(users, pageable,  size);

    when(userService.queryUserByNameLikeIgnoreCaseOrEmail(userName, userEmail, pageable)).thenReturn(page);
    var usersFound = getUsersByNameOrEmailUseCase.execute(userName, userEmail, pageable);

    assertThat(usersFound).isNotNull();
    assertThat(usersFound.getSize()).isEqualTo(PAGE_SIZE);
    assertThat(usersFound.getTotalPages()).isEqualTo(size);
    assertThat(usersFound.getTotalElements()).isEqualTo(size);
    verify(emailValidator).validate(userEmail);
  }
}
