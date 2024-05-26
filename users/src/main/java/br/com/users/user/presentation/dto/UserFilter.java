package br.com.users.user.presentation.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.Email;

@Tag(name = "UserFilter", description = "DTO de entrada filtro de pesquisa de usuário")
public record UserFilter(
    @Schema(example = "Thomas Anderson", description = "Nome do condutor do veículo/usuário do aplicativo")
    String name,
    @Schema(example = "thomas.anderson@matrix.com", description = "email do condutor do veículo/usuário do aplicativo")
    @Email
    String email) {

}
