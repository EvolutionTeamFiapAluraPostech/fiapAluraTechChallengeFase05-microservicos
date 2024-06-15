package br.com.fiap.company.domain.messages;

public final class CompanyMessages {

  public static final String ENTER_DOCUMENT_NUMBER_MESSAGE = "Enter the document number.";
  public static final String DOCUMENT_NUMBER_ALREADY_EXISTS_MESSAGE = "Document number already exists. %s";
  public static final String DOCUMENT_NUMBER_ALREADY_EXISTS_IN_OTHER_COMPANY_MESSAGE = "Document number already exists in other company. %s";
  public static final String UUID_INVALID_MESSAGE = "Invalid UUID. %s";
  public static final String CPF_INVALID_MESSAGE = "Invalid CPF. %s";
  public static final String CNPJ_INVALID_MESSAGE = "Invalid CNPJ. %s";
  public static final String COMPANY_NOT_FOUND_WITH_ID_MESSAGE = "Company not found with ID. %s";

  private CompanyMessages() {
  }
}
