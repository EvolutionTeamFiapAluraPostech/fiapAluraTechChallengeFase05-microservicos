package br.com.fiap.order.infrastructure.httpclient.user.dto;

import br.com.fiap.order.domain.entity.User;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "UserOutputDto", description = "DTO de saída para representação de um usuário.")
public record UserDto(
    @Schema(example = "feea1d11-11b9-4e34-9848-e1174bb857e3", description = "Valid UUID.")
    String id,
    @Schema(example = "Thomas Anderson", description = "Nome do usuário.")
    String name,
    @Schema(example = "thomas.anderson@matrix.com", description = "email do usuário.")
    String email
) {

  public UserDto(User user) {
    this(user.getId() != null ? user.getId() : null,
        user.getName(),
        user.getSub());
  }

  public static UserDto from(User user) {
    return new UserDto(user);
  }
}
