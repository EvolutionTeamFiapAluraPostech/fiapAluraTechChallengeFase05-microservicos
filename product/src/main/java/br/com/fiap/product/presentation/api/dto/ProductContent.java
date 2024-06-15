package br.com.fiap.product.presentation.api.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import lombok.Getter;
import lombok.Setter;

@Tag(name = "ProductContent", description = "DTO de saída representação de um produto")
@Getter
@Setter
public class ProductContent {

  @Schema(description = "Lista de DTO de produtos")
  private List<ProductOutputDto> content;
}
