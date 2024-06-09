package br.com.users.user.presentation.dto.customvalidator;

import br.com.users.user.domain.enums.DocNumberType;
import br.com.users.user.domain.valueobject.CnpjNumber;
import br.com.users.user.domain.valueobject.CpfNumber;
import br.com.users.user.presentation.dto.PostUserInputDto;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class UserDocNumberTypeAndDocNumberValueValidator implements
    ConstraintValidator<CPFouCNPJ, PostUserInputDto> {

  @Override
  public boolean isValid(PostUserInputDto postUserInputDto, ConstraintValidatorContext context) {
    try {
      if (postUserInputDto.docNumberType().equals(DocNumberType.CPF.name())) {
        new CpfNumber(postUserInputDto.docNumber());
      } else {
        new CnpjNumber(postUserInputDto.docNumber());
      }
      return true;
    } catch (Exception e) {
      return false;
    }
  }
}
