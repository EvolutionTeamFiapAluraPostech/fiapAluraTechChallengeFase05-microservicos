package br.com.fiap.order.presentation.api.dto;

import br.com.fiap.order.domain.entity.OrderItem;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.math.BigDecimal;

@Tag(name = "OrderItemInputDto", description = "DTO de entrada de item de pedido.")
public record OrderItemInputDto(
    @Schema(example = "bae0fc3d-be9d-472a-bf03-7a7ee2411ce1", description = "Identificador único do produto.")
    @NotBlank
    String productId,
    String productSku,
    String productDescription,
    @Schema(example = "1", description = "Quantidade do produto.")
    @NotNull
    @Positive
    BigDecimal quantity,
    @Schema(example = "1.25", description = "Preço do produto.")
    @NotNull
    @Positive
    BigDecimal price
) {

  public OrderItemInputDto(OrderItem orderItem) {
    this(orderItem.getProductId().toString(), orderItem.getProductSku(),
        orderItem.getProductDescription(), orderItem.getQuantity(), orderItem.getPrice());
  }
}
