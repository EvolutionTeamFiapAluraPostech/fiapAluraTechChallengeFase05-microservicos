package br.com.users.user.application.usecase;

import br.com.users.user.application.validator.UserCpfAlreadyRegisteredValidator;
import br.com.users.user.application.validator.UserEmailAlreadyRegisteredValidator;
import br.com.users.user.application.validator.UserPasswordCompromisedValidator;
import br.com.users.user.application.validator.UserPasswordStrengthValidator;
import br.com.users.user.domain.entity.User;
import br.com.users.user.domain.service.UserService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CreateUserUseCase {

  private final UserService userService;
  private final UserEmailAlreadyRegisteredValidator userEmailAlreadyRegisteredValidator;
  private final UserPasswordStrengthValidator userPasswordStrengthValidator;
  private final PasswordEncoder passwordEncoder;
  private final UserCpfAlreadyRegisteredValidator userCpfAlreadyRegisteredValidator;
  private final UserPasswordCompromisedValidator userPasswordCompromisedValidator;

  public CreateUserUseCase(
      UserService userService,
      UserEmailAlreadyRegisteredValidator userEmailAlreadyRegisteredValidator,
      UserPasswordStrengthValidator userPasswordStrengthValidator,
      PasswordEncoder passwordEncoder,
      UserCpfAlreadyRegisteredValidator userCpfAlreadyRegisteredValidator,
      UserPasswordCompromisedValidator userPasswordCompromisedValidator) {
    this.userService = userService;
    this.userEmailAlreadyRegisteredValidator = userEmailAlreadyRegisteredValidator;
    this.userPasswordStrengthValidator = userPasswordStrengthValidator;
    this.passwordEncoder = passwordEncoder;
    this.userCpfAlreadyRegisteredValidator = userCpfAlreadyRegisteredValidator;
    this.userPasswordCompromisedValidator = userPasswordCompromisedValidator;
  }

  @Transactional
  public User execute(User user) {
    userPasswordStrengthValidator.validate(user.getPassword());
    userEmailAlreadyRegisteredValidator.validate(user.getEmail());
    userCpfAlreadyRegisteredValidator.validate(user.getDocNumber());
    userPasswordCompromisedValidator.validate(user.getPassword());
    var passwordEncoded = passwordEncoder.encode(user.getPassword());
    user.setPassword(passwordEncoded);
    return userService.save(user);
  }
}
