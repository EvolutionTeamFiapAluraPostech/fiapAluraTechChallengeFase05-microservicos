package br.com.fiap.payment.shared.testdata;

import br.com.fiap.payment.infrastructure.httpclient.company.dto.CompanyDto;
import java.util.UUID;

public final class CompanyTestData {

  public static final UUID DEFAULT_COMPANY_UUID = UUID.randomUUID();
  public static final String DEFAULT_COMPANY_ID = DEFAULT_COMPANY_UUID.toString();
  public static final String DEFAULT_COMPANY_NAME = "Matrix Company";
  public static final String DEFAULT_COMPANY_EMAIL = "matrix@matrix.com";
  public static final String DEFAULT_COMPANY_CNPJ_DOC_NUMBER = "41404629000184";
  public static final String DEFAULT_COMPANY_STREET = "Alameda Rio Claro";
  public static final String DEFAULT_COMPANY_ADDRESS_NUMBER = "190";
  public static final String DEFAULT_COMPANY_NEIGHBORHOOD = "Bela Vista";
  public static final String DEFAULT_COMPANY_CITY = "São Paulo";
  public static final String DEFAULT_COMPANY_STATE = "SP";
  public static final String DEFAULT_COMPANY_COUNTRY = "Brasil";
  public static final String DEFAULT_COMPANY_POSTAL_CODE = "01332010";
  public static final String DEFAULT_COMPANY_DOC_NUMBER_TYPE = "CNPJ";

  public static CompanyDto createCompanyDto() {
    return new CompanyDto(DEFAULT_COMPANY_ID, DEFAULT_COMPANY_NAME, DEFAULT_COMPANY_EMAIL,
        DEFAULT_COMPANY_CNPJ_DOC_NUMBER, DEFAULT_COMPANY_DOC_NUMBER_TYPE, DEFAULT_COMPANY_STREET,
        DEFAULT_COMPANY_ADDRESS_NUMBER, DEFAULT_COMPANY_NEIGHBORHOOD, DEFAULT_COMPANY_CITY,
        DEFAULT_COMPANY_STATE, DEFAULT_COMPANY_COUNTRY, DEFAULT_COMPANY_POSTAL_CODE);
  }

  public static CompanyDto createCompanyDto(String companyId) {
    return new CompanyDto(companyId, DEFAULT_COMPANY_NAME, DEFAULT_COMPANY_EMAIL,
        DEFAULT_COMPANY_CNPJ_DOC_NUMBER, DEFAULT_COMPANY_DOC_NUMBER_TYPE, DEFAULT_COMPANY_STREET,
        DEFAULT_COMPANY_ADDRESS_NUMBER, DEFAULT_COMPANY_NEIGHBORHOOD, DEFAULT_COMPANY_CITY,
        DEFAULT_COMPANY_STATE, DEFAULT_COMPANY_COUNTRY, DEFAULT_COMPANY_POSTAL_CODE);
  }

  private CompanyTestData() {
  }
}
