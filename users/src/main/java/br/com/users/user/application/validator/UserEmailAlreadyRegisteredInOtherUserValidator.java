package br.com.users.user.application.validator;

import static br.com.users.user.domain.messages.UserMessages.USER_EMAIL_ALREADY_EXISTS;

import br.com.users.user.domain.exception.DuplicatedException;
import br.com.users.user.domain.entity.User;
import br.com.users.user.domain.service.UserService;
import java.util.UUID;
import org.springframework.stereotype.Component;
import org.springframework.validation.FieldError;

@Component
public class UserEmailAlreadyRegisteredInOtherUserValidator {

  private final UserService userService;

  public UserEmailAlreadyRegisteredInOtherUserValidator(UserService userService) {
    this.userService = userService;
  }

  public void validate(String userUuid, String email) {
    var user = userService.findByEmail(email);
    if (user.isPresent() && emailAlreadyExistsInOtherUser(userUuid, user.get())) {
      throw new DuplicatedException(new FieldError(this.getClass().getSimpleName(), "email",
          USER_EMAIL_ALREADY_EXISTS.formatted(email)));
    }
  }

  private static boolean emailAlreadyExistsInOtherUser(String userUuid, User user) {
    return !user.getId().equals(UUID.fromString(userUuid));
  }
}
