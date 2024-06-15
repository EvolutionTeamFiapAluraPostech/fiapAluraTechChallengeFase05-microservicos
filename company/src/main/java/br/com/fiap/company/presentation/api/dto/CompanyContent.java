package br.com.fiap.company.presentation.api.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import lombok.Getter;
import lombok.Setter;

@Tag(name = "CompanyContent", description = "DTO de saída representação de uma empresa")
@Getter
@Setter
public class CompanyContent {

  @Schema(description = "Lista de DTO de empresas")
  private List<CompanyOutputDto> content;
}
