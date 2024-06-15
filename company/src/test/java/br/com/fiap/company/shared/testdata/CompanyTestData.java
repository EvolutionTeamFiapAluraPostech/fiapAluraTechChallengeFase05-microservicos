package br.com.fiap.company.shared.testdata;

import br.com.fiap.company.domain.entity.Company;
import br.com.fiap.company.domain.enums.DocNumberType;
import java.math.BigDecimal;
import java.util.UUID;

public final class CompanyTestData {

  public static final String DEFAULT_COMPANY_NAME = "Matrix Company";
  public static final String DEFAULT_COMPANY_EMAIL = "matrix@matrix.com";
  public static final String DEFAULT_COMPANY_CPF_DOC_NUMBER = "11955975094";
  public static final String DEFAULT_COMPANY_CNPJ_DOC_NUMBER = "41404629000184";
  public static final String DEFAULT_COMPANY_STREET = "Alameda Rio Claro";
  public static final String DEFAULT_COMPANY_ADDRESS_NUMBER = "190";
  public static final String DEFAULT_COMPANY_NEIGHBORHOOD = "Bela Vista";
  public static final String DEFAULT_COMPANY_CITY = "São Paulo";
  public static final String DEFAULT_COMPANY_STATE = "SP";
  public static final String DEFAULT_COMPANY_COUNTRY = "Brasil";
  public static final String DEFAULT_COMPANY_POSTAL_CODE = "01332010";
  public static final String ALTERNATIVE_COMPANY_NAME = "Neo";
  public static final String ALTERNATIVE_COMPANY_EMAIL = "neo@matrix.com";
  public static final String ALTERNATIVE_COMPANY_CPF_DOC_NUMBER = "64990860020";
  public static final BigDecimal DEFAULT_COMPANY_LATITUDE = new BigDecimal("-23.56388");
  public static final BigDecimal DEFAULT_COMPANY_LONGITUDE = new BigDecimal("-46.65241");

  public static Company createNewCompany() {
    return Company.builder()
        .active(true)
        .name(DEFAULT_COMPANY_NAME)
        .email(DEFAULT_COMPANY_EMAIL)
        .docNumber(DEFAULT_COMPANY_CPF_DOC_NUMBER)
        .docNumberType(DocNumberType.CPF)
        .street(DEFAULT_COMPANY_STREET)
        .number(DEFAULT_COMPANY_ADDRESS_NUMBER)
        .neighborhood(DEFAULT_COMPANY_NEIGHBORHOOD)
        .city(DEFAULT_COMPANY_CITY)
        .state(DEFAULT_COMPANY_STATE)
        .country(DEFAULT_COMPANY_COUNTRY)
        .postalCode(DEFAULT_COMPANY_POSTAL_CODE)
        .latitude(DEFAULT_COMPANY_LATITUDE)
        .longitude(DEFAULT_COMPANY_LONGITUDE)
        .build();
  }

  public static Company createCompany() {
    var company = createNewCompany();
    company.setId(UUID.randomUUID());
    return company;
  }

  private CompanyTestData(){
  }
}
