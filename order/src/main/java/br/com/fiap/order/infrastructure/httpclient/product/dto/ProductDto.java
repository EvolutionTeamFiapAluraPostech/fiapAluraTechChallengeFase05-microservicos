package br.com.fiap.order.infrastructure.httpclient.product.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.math.BigDecimal;

@Tag(name = "ProductDto", description = "DTO de dados do produto.")
public record ProductDto(
    @Schema(example = "bae0fc3d-be9d-472a-bf03-7a7ee2411ce1", description = "Identificador único do produto no banco de dados.")
    String id,
    @Schema(example = "bae0fc3d-be9d-472a-bf03-7a7ee2411ce1", description = "Identificador único do produto na loja.")
    String sku,
    @Schema(example = "Teclado ergonômico ABNT 2", description = "Descrição do produto.")
    String description,
    @Schema(example = "UN", description = "Unidade de medida do produto.")
    String unitMeasurement,
    @Schema(example = "20.00", description = "Quantidade em estoque do produto.")
    BigDecimal quantityStock,
    @Schema(example = "315.00", description = "Preço unitário do produto.")
    BigDecimal price
) {

  public ProductDto(String id, String sku, String description) {
    this(id, sku, description, "", BigDecimal.ZERO, BigDecimal.ZERO);
  }
}
