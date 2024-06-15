package br.com.fiap.company.presentation.api.dto;

import br.com.fiap.company.domain.entity.Company;
import br.com.fiap.company.domain.enums.DocNumberType;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;

@Tag(name = "CompanyInputDto", description = "DTO de entrada de dados da empresa.")
public record CompanyInputDto(
    @Schema(example = "Thomas Anderson", description = "Nome da empresa.", minLength = 3, maxLength = 500)
    @NotBlank
    @Size(min = 3, max = 500, message = "Size must be between 3 e 500 characters.")
    String name,
    @Schema(example = "thomas.anderson@itcompany.com", description = "Endereço de e-mail da empresa.", minLength = 3, maxLength = 500)
    @NotBlank
    @Size(min = 3, max = 500, message = "Size must be between 3 e 500 characters.")
    @Email
    String email,
    @Schema(example = "11955975094", description = "Número do documento da empresa.", minLength = 11, maxLength = 14)
    @NotBlank
    @Size(min = 11, max = 14, message = "Size must be between 11 e 14 characters.")
    String docNumber,
    @Schema(example = "CPF ou CNPJ", description = "Tipo do número do documento da empresa.")
    DocNumberType docNumberType,
    @Schema(example = "Av. Lins de Vasconcelos", description = "Rua do endereço da empresa.", minLength = 3, maxLength = 255)
    @NotBlank
    @Size(min = 3, max = 255, message = "Size must be between 3 e 255 characters.")
    String street,
    @Schema(example = "1222", description = "Número do endereço da empresa.", minLength = 3, maxLength = 255)
    @NotBlank
    @Size(min = 3, max = 100, message = "Size must be between 3 e 100 characters.")
    String number,
    @Schema(example = "Cambuci", description = "Bairro do endereço da empresa.", minLength = 3, maxLength = 100)
    @NotBlank
    @Size(min = 3, max = 100, message = "Size must be between 3 e 100 characters.")
    String neighborhood,
    @Schema(example = "São Paulo", description = "Cidade do endereço da empresa.", minLength = 3, maxLength = 100)
    @NotBlank
    @Size(min = 3, max = 100, message = "Size must be between 3 e 100 characters.")
    String city,
    @Schema(example = "SP", description = "Sigla do Estado do endereço da empresa.", minLength = 2, maxLength = 2)
    @NotBlank
    @Size(min = 2, max = 2, message = "Size must be 2 characters.")
    String state,
    @Schema(example = "Brasil", description = "País do endereço da empresa.", minLength = 3, maxLength = 100)
    @NotBlank
    @Size(min = 3, max = 100, message = "Size must be between 3 e 100 characters.")
    String country,
    @Schema(example = "01538001", description = "Código postal do endereço da empresa.", minLength = 8, maxLength = 8)
    @NotBlank
    @Size(min = 8, max = 8, message = "Size must be between 8 characters.")
    @Digits(integer = 8, fraction = 0, message = "Postal code must be a valid number with a maximum of 8 integral digits")
    String postalCode,
    @Schema(example = "-23.56391", description = "Latitude.", minLength = -90, maxLength = 90)
    BigDecimal latitude,
    @Schema(example = "-46.65239", description = "Longitude.", minLength = -90, maxLength = 90)
    BigDecimal longitude
) {

  public Company from(CompanyInputDto companyInputDto) {
    return Company.builder()
        .name(companyInputDto.name)
        .email(companyInputDto.email)
        .docNumber(companyInputDto.docNumber)
        .docNumberType(companyInputDto.docNumberType)
        .street(companyInputDto.street)
        .number(companyInputDto.number)
        .neighborhood(companyInputDto.neighborhood)
        .city(companyInputDto.city)
        .state(companyInputDto.state)
        .country(companyInputDto.country)
        .postalCode(companyInputDto.postalCode)
        .latitude(companyInputDto.latitude)
        .longitude(companyInputDto.longitude)
        .build();
  }
}
