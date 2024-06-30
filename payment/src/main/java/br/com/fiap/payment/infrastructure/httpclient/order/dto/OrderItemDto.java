package br.com.fiap.payment.infrastructure.httpclient.order.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.math.BigDecimal;

@Tag(name = "OrderItemOutputDto", description = "DTO de saída do item do pedido.")
public record OrderItemDto(
    @Schema(example = "bae0fc3d-be9d-472a-bf03-7a7ee2411ce1", description = "Identificador único do item do pedido.")
    String id,
    @Schema(example = "bae0fc3d-be9d-472a-bf03-7a7ee2411ce1", description = "Identificador único do item do produto.")
    @NotNull
    String productId,
    @Schema(example = "10.00", description = "Quantidade do produto.")
    @Positive
    BigDecimal quantity,
    @Schema(example = "315.00", description = "Preço unitário do produto.")
    @Positive
    BigDecimal price,
    @Schema(example = "3150.00", description = "Valor total do item do pedido.")
    BigDecimal totalAmout
) {

  public OrderItemDto(String id, String productId, BigDecimal quantity, BigDecimal price) {
    this(id, productId, quantity, price, quantity.multiply(price));
  }
}
