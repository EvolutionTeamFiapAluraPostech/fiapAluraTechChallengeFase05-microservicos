package br.com.users.user.presentation.dto;

import br.com.users.user.domain.entity.User;
import br.com.users.user.domain.enums.DocNumberType;
import br.com.users.user.presentation.dto.customvalidator.CPFouCNPJ;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.NotBlank;
import org.hibernate.validator.constraints.Length;

@CPFouCNPJ
@Tag(name = "PostUserInputDto", description = "DTO de entrada representação de um usuário")
public class PostUserInputDto extends UserInputDto {

  @Schema(example = "@Admin123", description = "Senha do usuário.")
  @NotBlank(message = "Password is required.")
  @Length(min = 8, max = 20, message = "Min password length is 8 characters and max password length is 20 characters.")
  private final String password;

  public PostUserInputDto(String name, String email, String docNumberType, String docNumber, String password) {
    super(name, email, docNumberType, docNumber);
    this.password = password;
  }

  public static User toUser(PostUserInputDto postUserInputDto) {
    return User.builder()
        .name(postUserInputDto.getName())
        .email(postUserInputDto.getEmail())
        .docNumberType(DocNumberType.valueOf(postUserInputDto.getDocNumberType()))
        .docNumber(postUserInputDto.getDocNumber())
        .password(postUserInputDto.password)
        .build();
  }
}
