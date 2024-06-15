package br.com.fiap.order.presentation.api.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "OrderFilter", description = "DTO de entrada filtro de pesquisa de pedidos")
public record OrderFilter(
    @Schema(example = "bae0fc3d-be9d-472a-bf03-7a7ee2411ce1", description = "Identificador único da empresa.")
    String companyId,
    @Schema(example = "bae0fc3d-be9d-472a-bf03-7a7ee2411ce1", description = "Identificador único da cliente.")
    String customerId
) {

}
