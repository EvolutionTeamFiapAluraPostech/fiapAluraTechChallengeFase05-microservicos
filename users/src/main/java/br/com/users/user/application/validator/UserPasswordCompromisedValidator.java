package br.com.users.user.application.validator;

import br.com.users.user.domain.exception.ValidatorException;
import org.springframework.security.authentication.password.CompromisedPasswordChecker;
import org.springframework.stereotype.Component;
import org.springframework.validation.FieldError;

@Component
public class UserPasswordCompromisedValidator {

  private final CompromisedPasswordChecker compromisedPasswordChecker;

  public UserPasswordCompromisedValidator(CompromisedPasswordChecker compromisedPasswordChecker) {
    this.compromisedPasswordChecker = compromisedPasswordChecker;
  }

  public void validate(String password) {
    var decision = compromisedPasswordChecker.check(password);
    if (decision.isCompromised()) {
      throw new ValidatorException(new FieldError(this.getClass().getSimpleName(), "user.password",
          "The provided password is compromised and cannot be used."));
    }
  }
}
