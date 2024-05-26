package br.com.users.user.presentation.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import lombok.Getter;
import lombok.Setter;

@Tag(name = "UserContent", description = "DTO de saída represetação de um usuário")
@Getter
@Setter
public class UserContent {

  @Schema(description = "Lista de DTO de usuários")
  private List<UserOutputDto> content;
}
