package br.com.fiap.order.infrastructure.httpclient.customer.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "CustomerDto", description = "DTO de dados do cliente.")
public record CustomerDto(
    @Schema(example = "bae0fc3d-be9d-472a-bf03-7a7ee2411ce1", description = "Identificador único do cliente.")
    String id,
    @Schema(example = "Thomas Anderson", description = "Nome do cliente.", minLength = 3, maxLength = 500)
    String name,
    @Schema(example = "thomas.anderson@itcompany.com", description = "Endereço de e-mail do cliente.", minLength = 3, maxLength = 500)
    String email,
    @Schema(example = "11955975094", description = "Número do documento do cliente.", minLength = 11, maxLength = 14)
    String docNumber,
    @Schema(example = "CPF ou CNPJ", description = "Tipo do número do documento do cliente.")
    String docNumberType,
    @Schema(example = "Av. Lins de Vasconcelos", description = "Rua do endereço do cliente.", minLength = 3, maxLength = 255)
    String street,
    @Schema(example = "1222", description = "Número do endereço do cliente.", minLength = 3, maxLength = 255)
    String number,
    @Schema(example = "Cambuci", description = "Bairro do endereço do cliente.", minLength = 3, maxLength = 100)
    String neighborhood,
    @Schema(example = "São Paulo", description = "Cidade do endereço do cliente.", minLength = 3, maxLength = 100)
    String city,
    @Schema(example = "SP", description = "Sigla do Estado do endereço do cliente.", minLength = 2, maxLength = 2)
    String state,
    @Schema(example = "Brasil", description = "País do endereço do cliente.", minLength = 3, maxLength = 100)
    String country,
    @Schema(example = "01538001", description = "Código postal do endereço do cliente.", minLength = 8, maxLength = 8)
    String postalCode
) {

  public CustomerDto(String id, String name) {
    this(id, name, "", "", "", "", "", "", "", "", "", "");
  }
}
