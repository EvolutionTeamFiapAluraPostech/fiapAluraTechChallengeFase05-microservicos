package br.com.fiap.order.infrastructure.httpclient.logistics.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;

@Tag(name = "LogisticOrderDto", description = "DTO de saída de pedido.")
public record LogisticOrderDto(
    @Schema(example = "bae0fc3d-be9d-472a-bf03-7a7ee2411ce1", description = "Identificador único da logística do pedido.")
    String id,
    @Schema(example = "bae0fc3d-be9d-472a-bf03-7a7ee2411ce1", description = "Identificador único do pedido.")
    String orderId,
    @Schema(example = "bae0fc3d-be9d-472a-bf03-7a7ee2411ce1", description = "Identificador único da empresa.")
    String companyId,
    @Schema(example = "bae0fc3d-be9d-472a-bf03-7a7ee2411ce1", description = "Identificador único do cliente.")
    String customerId,
    @Schema(example = "{[]}", description = "Lista de produtos do pedido.")
    List<LogisticOrderItemDto> logisticsItems
) {

}
