package br.com.fiap.product.presentation.api.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "ProductFilter", description = "DTO de entrada filtro de pesquisa de produtos")
public record ProductFilter(
    @Schema(example = "Key/BR-/Erg/Bla", description = "Código sku do produto")
    String sku,
    @Schema(example = "Keyboard Ergonomic Black", description = "Descrição do produto")
    String description
) {
}
