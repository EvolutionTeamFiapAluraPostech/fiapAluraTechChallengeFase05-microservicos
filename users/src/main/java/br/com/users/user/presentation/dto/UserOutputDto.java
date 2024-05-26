package br.com.users.user.presentation.dto;

import br.com.users.user.domain.entity.User;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.data.domain.Page;

@Tag(name = "UserOutputDto", description = "DTO de saída para representação de um usuário.")
public record UserOutputDto(
    @Schema(example = "feea1d11-11b9-4e34-9848-e1174bb857e3", description = "Valid UUID.")
    String id,
    @Schema(example = "Thomas Anderson", description = "Nome do usuário.")
    String name,
    @Schema(example = "thomas.anderson@matrix.com", description = "email do usuário.")
    String email,
    @Schema(example = "CPF ou CNPJ", description = "Tipo do número do documento do usuário.")
    String docNumberType,
    @Schema(example = "92477979000", description = "CPF do usuário.")
    String docNumber
) {

  public UserOutputDto(User user) {
    this(user.getId() != null ? user.getId().toString() : null,
        user.getName(),
        user.getEmail(),
        user.getDocNumberType().name(),
        user.getDocNumber());
  }

  public static Page<UserOutputDto> toPage(Page<User> usersPage) {
    return usersPage.map(UserOutputDto::new);
  }

  public static UserOutputDto from(User user) {
    return new UserOutputDto(user);
  }
}
