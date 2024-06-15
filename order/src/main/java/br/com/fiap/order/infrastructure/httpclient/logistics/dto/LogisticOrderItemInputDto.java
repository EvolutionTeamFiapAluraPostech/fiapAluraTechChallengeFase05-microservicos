package br.com.fiap.order.infrastructure.httpclient.logistics.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.math.BigDecimal;

@Tag(name = "LogisticOrderItemInputDto", description = "DTO de saída do item do pedido.")
public record LogisticOrderItemInputDto(
    @Schema(example = "bae0fc3d-be9d-472a-bf03-7a7ee2411ce1", description = "Identificador único do item do pedido.")
    String orderItemId,
    @Schema(example = "bae0fc3d-be9d-472a-bf03-7a7ee2411ce1", description = "Identificador único do item do produto.")
    String productId,
    @Schema(example = "bae0fc3d-be9d-472a-bf03-7a7ee2411ce1", description = "Identificador único do produto na loja.")
    String productSku,
    @Schema(example = "Teclado ergonômico ABNT 2", description = "Descrição do produto.")
    String productDescription,
    @Schema(example = "10.00", description = "Quantidade do produto.")
    BigDecimal quantity,
    @Schema(example = "315.00", description = "Preço unitário do produto.")
    BigDecimal price
) {

}
