package br.com.users.user.presentation.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

@Tag(name = "AuthenticateInputDto", description = "DTO de entrada autenticação do usuário")
public record AuthenticateInputDto(
    @Schema(example = "thomas.anderson@matrix.com", description = "email do condutor do veículo/usuário do aplicativo")
    @NotBlank
    @Email
    String email,
    @Schema(example = "@Admin123", description = "Senha do condutor do veículo/usuário do aplicativo")
    @NotBlank
    String password) {
}
