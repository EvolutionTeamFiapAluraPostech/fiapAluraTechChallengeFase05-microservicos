package br.com.fiap.product.presentation.api.dto;

import br.com.fiap.product.domain.entity.Product;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;

@Tag(name = "ProductOutputDto", description = "DTO de saída de dados do produto.")
public record ProductInputDto(
    @Schema(example = "bae0fc3d-be9d-472a-bf03-7a7ee2411ce1", description = "Identificador único do produto na loja.")
    @NotBlank
    @Size(min = 1, max = 20, message = "O tamanho do sku deve estar entre 1 e 20")
    String sku,
    @Schema(example = "Teclado ergonômico ABNT 2", description = "Descrição do produto.")
    @NotBlank
    @Size(min = 3, max = 500, message = "O tamanho da descrição deve estar entre 3 e 500")
    String description,
    @Schema(example = "UN", description = "Unidade de medida do produto.")
    @NotBlank
    @Size(min = 1, max = 20, message = "O tamanho da unidade de medida deve estar entre 1 e 20")
    String unitMeasurement,
    @Schema(example = "20.00", description = "Quantidade em estoque do produto.")
    @Positive
    BigDecimal quantityStock,
    @Schema(example = "315.00", description = "Preço unitário do produto.")
    @Positive
    BigDecimal price,
    @Schema(example = "https://m.media-amazon.com/images/I/71Yp7pxBFOL._AC_SX522_.jpg", description = "URL da image do produto.")
    String imageUrl
) {

  public Product toProduct() {
    return Product.builder()
        .active(true)
        .sku(this.sku)
        .description(this.description)
        .unitMeasurement(this.unitMeasurement)
        .quantityStock(this.quantityStock)
        .price(this.price)
        .imageUrl(this.imageUrl)
        .build();
  }
}
