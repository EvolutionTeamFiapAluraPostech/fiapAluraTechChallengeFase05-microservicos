package br.com.users.user.presentation.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "TokenJwtInput", description = "DTO de representação de um token JWT")
public record TokenJwtInput(
    @Schema(description = "Token JWT")
    String token
) {

}
