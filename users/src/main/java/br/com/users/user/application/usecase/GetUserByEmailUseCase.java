package br.com.users.user.application.usecase;

import static br.com.users.user.domain.messages.UserMessages.USER_EMAIL_NOT_FOUND;

import br.com.users.user.domain.exception.NoResultException;
import br.com.users.user.domain.validator.EmailValidator;
import br.com.users.user.domain.entity.User;
import br.com.users.user.domain.service.UserService;
import org.springframework.stereotype.Service;
import org.springframework.validation.FieldError;

@Service
public class GetUserByEmailUseCase {

  private final UserService userService;
  private final EmailValidator emailValidator;

  public GetUserByEmailUseCase(
      UserService userService,
      EmailValidator emailValidator) {
    this.userService = userService;
    this.emailValidator = emailValidator;
  }

  public User execute(String email) {
    emailValidator.validate(email);
    return userService.findByEmail(email).orElseThrow(
        () -> new NoResultException(new FieldError(this.getClass().getSimpleName(), "User",
            USER_EMAIL_NOT_FOUND.formatted(email))));
  }
}
