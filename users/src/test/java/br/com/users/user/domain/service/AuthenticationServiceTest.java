package br.com.users.user.domain.service;

import static br.com.users.shared.testData.user.UserTestData.DEFAULT_USER_EMAIL;
import static br.com.users.shared.testData.user.UserTestData.createUser;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class AuthenticationServiceTest {

  @Mock
  private UserService userService;
  @InjectMocks
  private AuthenticationService authenticationService;

  @Test
  void shouldLoadUserByUsername() {
    var user = createUser();

    when(userService.findByEmailRequired(user.getEmail())).thenReturn(user);
    var userFound = authenticationService.loadUserByUsername(user.getUsername());

    assertThat(userFound).isNotNull();
    assertThat(userFound).usingRecursiveComparison().isEqualTo(user);
  }

  @Test
  void shouldThrowExceptionWhenUserIsNotFound() {
    when(userService.findByEmailRequired(DEFAULT_USER_EMAIL)).thenReturn(null);
    var userFound = authenticationService.loadUserByUsername(DEFAULT_USER_EMAIL);

    assertThat(userFound).isNull();
  }
}