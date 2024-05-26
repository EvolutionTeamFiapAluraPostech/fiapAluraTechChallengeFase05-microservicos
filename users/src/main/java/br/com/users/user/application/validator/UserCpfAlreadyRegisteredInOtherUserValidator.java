package br.com.users.user.application.validator;

import static br.com.users.user.domain.messages.UserMessages.USER_CPF_ALREADY_EXISTS;

import br.com.users.user.domain.exception.DuplicatedException;
import br.com.users.user.domain.entity.User;
import br.com.users.user.domain.service.UserService;
import java.util.UUID;
import org.springframework.stereotype.Component;
import org.springframework.validation.FieldError;

@Component
public class UserCpfAlreadyRegisteredInOtherUserValidator {

  private final UserService userService;

  public UserCpfAlreadyRegisteredInOtherUserValidator(UserService userService) {
    this.userService = userService;
  }

  public void validate(String userUuid, String cpf) {
    var user = userService.findByCpf(cpf);
    if (user.isPresent() && cpfAlreadyExistsInOtherUser(userUuid, user.get())) {
      throw new DuplicatedException(new FieldError(this.getClass().getSimpleName(), "cpf",
          USER_CPF_ALREADY_EXISTS.formatted(cpf)));
    }
  }

  private static boolean cpfAlreadyExistsInOtherUser(String userUuid, User user) {
    return !user.getId().equals(UUID.fromString(userUuid));
  }
}
