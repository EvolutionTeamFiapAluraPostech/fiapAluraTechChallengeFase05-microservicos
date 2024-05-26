package br.com.users.user.presentation.dto;

import br.com.users.user.domain.entity.User;
import br.com.users.user.domain.enums.DocNumberType;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import org.hibernate.validator.constraints.Length;

@Tag(name = "PostUserInputDto", description = "DTO de entrada representação de um usuário")
public record PostUserInputDto(
    @Schema(example = "Thomas Anderson", description = "Nome do usuário.")
    @NotBlank(message = "Name is required.")
    @Length(max = 500, message = "Max name length is 500 characters.")
    String name,
    @Schema(example = "thomas.anderson@matrix.com", description = "email do usuário.")
    @NotBlank(message = "email is required.")
    @Length(max = 500, message = "Max email length is 500 characters.")
    @Email
    String email,
    @Schema(example = "CPF ou CNPJ", description = "CPF/CNPJ do usuário.")
    @NotBlank(message = "Document number type is required.")
    @Length(max = 4, message = "Max cpf length is 4 characters.")
    String docNumberType,
    @Schema(example = "92477979000", description = "CPF/CNPJ do usuário.")
    @NotBlank(message = "cpf is required.")
    @Length(max = 14, message = "Max cpf length is 14 characters.")
    String docNumber,
    @Schema(example = "@Admin123", description = "Senha do usuário.")
    @NotBlank(message = "Password is required.")
    @Length(min = 8, max = 20, message = "Min password length is 8 characters and max password length is 20 characters.")
    String password
) {

  public static User toUser(PostUserInputDto postUserInputDto) {
    return User.builder()
        .name(postUserInputDto.name)
        .email(postUserInputDto.email)
        .docNumberType(DocNumberType.valueOf(postUserInputDto.docNumberType))
        .docNumber(postUserInputDto.docNumber)
        .password(postUserInputDto.password)
        .build();
  }
}
