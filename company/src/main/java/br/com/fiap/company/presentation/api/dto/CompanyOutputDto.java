package br.com.fiap.company.presentation.api.dto;

import br.com.fiap.company.domain.entity.Company;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.math.BigDecimal;
import org.springframework.data.domain.Page;

@Tag(name = "CompanyOutputDto", description = "DTO de saída de dados da empresa.")
public record CompanyOutputDto(
    @Schema(example = "bae0fc3d-be9d-472a-bf03-7a7ee2411ce1", description = "Identificador único da empresa.")
    String id,
    @Schema(example = "Thomas Anderson", description = "Nome da empresa.", minLength = 3, maxLength = 500)
    String name,
    @Schema(example = "thomas.anderson@itcompany.com", description = "Endereço de e-mail da empresa.", minLength = 3, maxLength = 500)
    String email,
    @Schema(example = "11955975094", description = "Número do documento da empresa.", minLength = 11, maxLength = 14)
    String docNumber,
    @Schema(example = "CPF ou CNPJ", description = "Tipo do número do documento da empresa.")
    String docNumberType,
    @Schema(example = "Av. Lins de Vasconcelos", description = "Rua do endereço da empresa.", minLength = 3, maxLength = 255)
    String street,
    @Schema(example = "1222", description = "Número do endereço da empresa.", minLength = 3, maxLength = 255)
    String number,
    @Schema(example = "Cambuci", description = "Bairro do endereço da empresa.", minLength = 3, maxLength = 100)
    String neighborhood,
    @Schema(example = "São Paulo", description = "Cidade do endereço da empresa.", minLength = 3, maxLength = 100)
    String city,
    @Schema(example = "SP", description = "Sigla do Estado do endereço da empresa.", minLength = 2, maxLength = 2)
    String state,
    @Schema(example = "Brasil", description = "País do endereço da empresa.", minLength = 3, maxLength = 100)
    String country,
    @Schema(example = "01538001", description = "Código postal do endereço da empresa.", minLength = 8, maxLength = 8)
    String postalCode,
    @Schema(example = "-23.56391", description = "Latitude.", minLength = -90, maxLength = 90)
    BigDecimal latitude,
    @Schema(example = "-46.65239", description = "Longitude.", minLength = -90, maxLength = 90)
    BigDecimal longitude
) {

  public CompanyOutputDto(Company company) {
    this(company.getId() != null ? company.getId().toString() : null,
        company.getName(),
        company.getEmail(),
        company.getDocNumber(),
        company.getDocNumberType().name(),
        company.getStreet(),
        company.getNumber(),
        company.getNeighborhood(),
        company.getCity(),
        company.getState(),
        company.getCountry(),
        company.getPostalCode(),
        company.getLatitude(),
        company.getLongitude()
    );
  }

  public static CompanyOutputDto toCompanyOutputDtoFrom(Company company) {
    return new CompanyOutputDto(company.getId() != null ? company.getId().toString() : "",
        company.getName(), company.getEmail(), company.getDocNumber(),
        company.getDocNumberType().name(), company.getStreet(), company.getNumber(),
        company.getNeighborhood(), company.getCity(), company.getState(),
        company.getCountry(), company.getPostalCode(), company.getLatitude(), company.getLongitude());
  }

  public static Page<CompanyOutputDto> toPage(Page<Company> companyPage) {
    return companyPage.map(CompanyOutputDto::new);
  }
}
