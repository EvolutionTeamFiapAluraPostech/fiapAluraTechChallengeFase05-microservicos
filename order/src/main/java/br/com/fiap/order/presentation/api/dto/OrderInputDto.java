package br.com.fiap.order.presentation.api.dto;

import br.com.fiap.order.domain.entity.Order;
import br.com.fiap.order.domain.entity.OrderItem;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Tag(name = "OrderInputDto", description = "DTO de entrada de pedido.")
public record OrderInputDto(
    @Schema(example = "bae0fc3d-be9d-472a-bf03-7a7ee2411ce1", description = "Identificador único da empresa.")
    @NotBlank
    String companyId,
    @Schema(example = "bae0fc3d-be9d-472a-bf03-7a7ee2411ce1", description = "Identificador único do usuário usuário cliente.")
    @NotBlank
    String customerId,
    @Schema(example = "{[]}", description = "Lista de produtos do pedido.")
    @NotEmpty
    @Size(min = 1)
    List<@Valid OrderItemInputDto> orderItems
) {

  public Order toOrder() {
    var orderItems = new ArrayList<OrderItem>();

    this.orderItems.forEach(orderItemInputDto -> {
      var orderItem = OrderItem.builder()
          .productId(!orderItemInputDto.productId().isEmpty() ? UUID.fromString(
              orderItemInputDto.productId()) : null)
          .productSku(orderItemInputDto.productSku())
          .quantity(orderItemInputDto.quantity())
          .price(orderItemInputDto.price())
          .build();
      orderItems.add(orderItem);
    });

    return Order.builder()
        .companyId(UUID.fromString(this.companyId))
        .customerId(UUID.fromString(this.customerId))
        .orderItems(orderItems)
        .build();
  }
}
