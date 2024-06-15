package br.com.fiap.order.presentation.api.dto;

import br.com.fiap.order.domain.entity.Order;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.ArrayList;
import java.util.List;
import org.springframework.data.domain.Page;

@Tag(name = "OrderDto", description = "DTO de saída de pedido.")
public record OrderOutputDto(
    @Schema(example = "bae0fc3d-be9d-472a-bf03-7a7ee2411ce1", description = "Identificador único do pedido.")
    String id,
    @Schema(example = "bae0fc3d-be9d-472a-bf03-7a7ee2411ce1", description = "Identificador único da empresa.")
    String companyId,
    @Schema(example = "bae0fc3d-be9d-472a-bf03-7a7ee2411ce1", description = "Identificador único do cliente.")
    String customerId,
    @Schema(example = "{[]}", description = "Lista de produtos do pedido.")
    List<OrderItemDto> orderItems
) {

  public OrderOutputDto(Order order) {
    this(order.getId().toString(), order.getCompanyId().toString(),
        order.getCustomerId().toString(),
        OrderItemDto.getOrderItemsOutputDtofrom(order.getOrderItems()));
  }

  public static OrderOutputDto from(Order order) {
    var orderItemsDto = new ArrayList<OrderItemDto>();
    order.getOrderItems().forEach(orderItem -> {
      var orderItemOutputDto = new OrderItemDto(orderItem.getId().toString(),
          orderItem.getProductId().toString(),
          orderItem.getQuantity(),
          orderItem.getPrice(),
          orderItem.getTotalAmount());
      orderItemsDto.add(orderItemOutputDto);
    });

    return new OrderOutputDto(order.getId() != null ? order.getId().toString() : null,
        order.getCompanyId().toString(), order.getCustomerId().toString(),
        orderItemsDto);
  }

  public static Page<OrderOutputDto> toPage(Page<Order> ordersPage) {
    return ordersPage.map(OrderOutputDto::new);
  }
}
