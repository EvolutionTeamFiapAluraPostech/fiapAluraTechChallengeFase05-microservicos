package br.com.users.user.application.usecase;

import static br.com.users.user.domain.messages.UserMessages.USER_CPF_NOT_FOUND;

import br.com.users.user.domain.exception.NoResultException;
import br.com.users.user.domain.entity.User;
import br.com.users.user.domain.service.UserService;
import org.springframework.stereotype.Service;
import org.springframework.validation.FieldError;

@Service
public class GetUserByCpfUseCase {

  private final UserService userService;

  public GetUserByCpfUseCase(UserService userService) {
    this.userService = userService;
  }

  public User execute(String cpf) {
    return userService.findByCpf(cpf).orElseThrow(
        () -> new NoResultException(new FieldError(this.getClass().getSimpleName(), "cpf",
            USER_CPF_NOT_FOUND.formatted(cpf))));
  }
}
