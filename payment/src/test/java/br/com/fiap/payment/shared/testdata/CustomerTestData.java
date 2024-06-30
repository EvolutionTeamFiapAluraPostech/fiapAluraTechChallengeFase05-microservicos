package br.com.fiap.payment.shared.testdata;

import br.com.fiap.payment.infrastructure.httpclient.customer.dto.CustomerDto;
import java.util.UUID;

public final class CustomerTestData {

  public static final UUID DEFAULT_USER_UUID = UUID.randomUUID();
  public static final String DEFAULT_USER_UUID_FROM_STRING = DEFAULT_USER_UUID.toString();
  public static final String DEFAULT_USER_NAME = "Morpheus";
  public static final String DEFAULT_USER_EMAIL = "morpheus@matrix.com";

  public static CustomerDto createCustomerDto() {
    return new CustomerDto(DEFAULT_USER_UUID_FROM_STRING, DEFAULT_USER_NAME, DEFAULT_USER_EMAIL);
  }
}
