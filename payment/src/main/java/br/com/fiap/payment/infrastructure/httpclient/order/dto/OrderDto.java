package br.com.fiap.payment.infrastructure.httpclient.order.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.math.BigDecimal;
import java.util.List;

@Tag(name = "OrderDto", description = "DTO de saída de pedido.")
public record OrderDto(
    @Schema(example = "bae0fc3d-be9d-472a-bf03-7a7ee2411ce1", description = "Identificador único do pedido.")
    String id,
    @Schema(example = "ENTREGUE", description = "Status do pedido.")
    String orderStatus,
    @Schema(example = "bae0fc3d-be9d-472a-bf03-7a7ee2411ce1", description = "Identificador único da empresa.")
    String companyId,
    @Schema(example = "bae0fc3d-be9d-472a-bf03-7a7ee2411ce1", description = "Identificador único do cliente.")
    String customerId,
    @Schema(example = "{[]}", description = "Lista de produtos do pedido.")
    List<OrderItemDto> orderItems
) {

  public BigDecimal calculateTotalAmount() {
    return orderItems().stream().map(OrderItemDto::totalAmout)
        .reduce(BigDecimal.ZERO, BigDecimal::add);
  }
}
