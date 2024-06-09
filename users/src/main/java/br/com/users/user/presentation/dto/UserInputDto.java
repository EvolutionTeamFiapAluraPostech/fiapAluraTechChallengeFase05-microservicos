package br.com.users.user.presentation.dto;

import br.com.users.user.domain.enums.DocNumberType;
import br.com.users.user.domain.validator.ValueOfEnum;
import br.com.users.user.presentation.dto.customvalidator.CPFouCNPJ;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import org.hibernate.validator.constraints.Length;

@CPFouCNPJ
public abstract class UserInputDto {

  @Schema(example = "Thomas Anderson", description = "Nome do usuário.")
  @NotBlank(message = "Name is required.")
  @Length(max = 500, message = "Max name length is 500 characters.")
  private final String name;
  @Schema(example = "thomas.anderson@matrix.com", description = "email do usuário.")
  @NotBlank(message = "email is required.")
  @Length(max = 500, message = "Max email length is 500 characters.")
  @Email
  private final String email;
  @Schema(example = "CPF ou CNPJ", description = "CPF/CNPJ do usuário.")
  @NotBlank(message = "Document number type is required.")
  @Length(max = 4, message = "Max cpf length is 4 characters.")
  @ValueOfEnum(enumClass = DocNumberType.class)
  private final String docNumberType;
  @Schema(example = "92477979000", description = "CPF/CNPJ do usuário.")
  @NotBlank(message = "cpf is required.")
  @Length(max = 14, message = "Max cpf length is 14 characters.")
  private final String docNumber;

  protected UserInputDto(String name, String email, String docNumberType, String docNumber) {
    this.name = name;
    this.email = email;
    this.docNumberType = docNumberType;
    this.docNumber = docNumber;
  }

  public String getName() {
    return name;
  }

  public String getEmail() {
    return email;
  }

  public String getDocNumberType() {
    return docNumberType;
  }

  public String getDocNumber() {
    return docNumber;
  }
}
