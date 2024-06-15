package br.com.fiap.company.presentation.api.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.Email;

@Tag(name = "CompanyFilter", description = "DTO de entrada filtro de pesquisa de empresas")
public record CompanyFilter(
    @Schema(example = "Thomas Anderson", description = "Nome da empresa")
    String name,
    @Schema(example = "thomas.anderson@itcompany.com", description = "email da empresa")
    @Email
    String email) {
}
