package br.com.fiap.product.presentation.api.dto;

import br.com.fiap.product.domain.entity.Product;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.math.BigDecimal;
import org.springframework.data.domain.Page;

@Tag(name = "ProductOutputDto", description = "DTO de saída de dados do produto.")
public record ProductOutputDto(
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

  public ProductOutputDto(Product product) {
    this(product.getId() != null ? product.getId().toString() : null,
        product.getSku(),
        product.getDescription(),
        product.getUnitMeasurement(),
        product.getQuantityStock(),
        product.getPrice());
  }

  public static ProductOutputDto from(Product product) {
    return new ProductOutputDto(product.getId() != null ? product.getId().toString() : null,
        product.getSku(), product.getDescription(), product.getUnitMeasurement(),
        product.getQuantityStock(), product.getPrice());
  }

  public static Page<ProductOutputDto> toPage(Page<Product> productPage) {
    return productPage.map(ProductOutputDto::new);
  }
}
