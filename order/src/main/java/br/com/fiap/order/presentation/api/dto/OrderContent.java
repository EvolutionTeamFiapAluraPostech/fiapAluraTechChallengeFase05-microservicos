package br.com.fiap.order.presentation.api.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import lombok.Getter;
import lombok.Setter;

@Tag(name = "OrderContent", description = "DTO de saída representação de um pedido")
@Getter
@Setter
public class OrderContent {

  @Schema(description = "Lista de DTO de pedidos")
  private List<OrderDto> content;
}
