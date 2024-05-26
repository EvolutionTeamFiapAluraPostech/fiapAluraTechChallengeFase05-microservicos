package br.com.users.user.application.usecase;

import static br.com.users.user.domain.messages.UserMessages.USER_ID_NOT_FOUND;

import br.com.users.user.domain.exception.NoResultException;
import br.com.users.user.domain.validator.UuidValidator;
import br.com.users.user.domain.entity.User;
import br.com.users.user.domain.service.UserService;
import java.util.UUID;
import org.springframework.stereotype.Service;
import org.springframework.validation.FieldError;

@Service
public class GetUserByIdUseCase {

  private final UserService userService;
  private final UuidValidator uuidValidator;

  public GetUserByIdUseCase(UserService userService, UuidValidator uuidValidator) {
    this.userService = userService;
    this.uuidValidator = uuidValidator;
  }

  public User execute(String uuid) {
    uuidValidator.validate(uuid);
    return userService.findById(UUID.fromString(uuid)).orElseThrow(
        () -> new NoResultException(new FieldError(this.getClass().getSimpleName(), "User",
            USER_ID_NOT_FOUND.formatted(uuid))));
  }
}
