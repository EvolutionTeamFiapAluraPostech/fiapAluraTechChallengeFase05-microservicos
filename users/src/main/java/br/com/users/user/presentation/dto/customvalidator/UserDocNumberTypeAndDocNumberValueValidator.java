package br.com.users.user.presentation.dto.customvalidator;

import br.com.users.user.domain.enums.DocNumberType;
import br.com.users.user.domain.valueobject.CnpjNumber;
import br.com.users.user.domain.valueobject.CpfNumber;
import br.com.users.user.presentation.dto.UserInputDto;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class UserDocNumberTypeAndDocNumberValueValidator implements
    ConstraintValidator<CPFouCNPJ, UserInputDto> {

  @Override
  public boolean isValid(UserInputDto userInputDto, ConstraintValidatorContext context) {
    try {
      if (userInputDto.getDocNumberType().equals(DocNumberType.CPF.name())) {
        new CpfNumber(userInputDto.getDocNumber());
      } else {
        new CnpjNumber(userInputDto.getDocNumber());
      }
      return true;
    } catch (Exception e) {
      return false;
    }
  }
}
