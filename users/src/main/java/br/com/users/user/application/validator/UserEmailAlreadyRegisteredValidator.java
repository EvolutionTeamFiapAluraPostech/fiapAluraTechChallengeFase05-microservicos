package br.com.users.user.application.validator;

import static br.com.users.user.domain.messages.UserMessages.USER_EMAIL_ALREADY_EXISTS;

import br.com.users.user.domain.exception.DuplicatedException;
import br.com.users.user.domain.service.UserService;
import org.springframework.stereotype.Component;
import org.springframework.validation.FieldError;

@Component
public class UserEmailAlreadyRegisteredValidator {

  private final UserService userService;

  public UserEmailAlreadyRegisteredValidator(UserService userService) {
    this.userService = userService;
  }

  public void validate(String email) {
    var user = userService.findByEmail(email);
    if (user.isPresent()) {
      throw new DuplicatedException(new FieldError(this.getClass().getSimpleName(), "email",
          USER_EMAIL_ALREADY_EXISTS.formatted(email)));
    }
  }
}
