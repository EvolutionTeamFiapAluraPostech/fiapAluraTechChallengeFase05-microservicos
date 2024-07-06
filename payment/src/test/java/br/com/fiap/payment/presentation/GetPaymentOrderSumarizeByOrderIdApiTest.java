package br.com.fiap.payment.presentation;

import static br.com.fiap.payment.shared.testdata.PaymentTestData.createNewPayment;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import br.com.fiap.payment.domain.entity.Payment;
import br.com.fiap.payment.shared.annotation.DatabaseTest;
import br.com.fiap.payment.shared.annotation.IntegrationTest;
import com.jayway.jsonpath.JsonPath;
import jakarta.persistence.EntityManager;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

@IntegrationTest
@DatabaseTest
class GetPaymentOrderSumarizeByOrderIdApiTest {

  private static final String URL_PAYMENTS = "/payments/order/{id}";
  private final MockMvc mockMvc;
  private final EntityManager entityManager;

  @Autowired
  GetPaymentOrderSumarizeByOrderIdApiTest(MockMvc mockMvc, EntityManager entityManager) {
    this.mockMvc = mockMvc;
    this.entityManager = entityManager;
  }

  @Test
  void shouldReturnBadRequestWhenGetPaymentOrderSummarizedByOrderIdAndOrderIdIsInvalid()
      throws Exception {
    var id = "1aB";
    var request = get(URL_PAYMENTS, id);

    mockMvc.perform(request)
        .andExpect(status().isBadRequest())
        .andExpect(content().contentType(APPLICATION_JSON));
  }

  @Test
  void shouldReturnNotFoundWhenGetPaymentOrderSummarizedByOrderIdAndOrderIdWasNotFound() throws Exception {
    var id = UUID.randomUUID().toString();
    var request = get(URL_PAYMENTS, id);

    mockMvc.perform(request)
        .andExpect(status().isNotFound())
        .andExpect(content().contentType(APPLICATION_JSON));
  }

  @Test
  void shouldGetPaymentOrderSummarizedByOrderId() throws Exception {
    var payment = entityManager.merge(createNewPayment());

    var request = get(URL_PAYMENTS, payment.getOrderId());
    var mockMvcResult = mockMvc.perform(request)
        .andExpect(MockMvcResultMatchers.status().isOk())
        .andExpect(MockMvcResultMatchers.content().contentType(APPLICATION_JSON))
        .andExpect(jsonPath("$.id", equalTo(payment.getId().toString())))
        .andReturn();

    var contentAsString = mockMvcResult.getResponse().getContentAsString();
    var id = JsonPath.parse(contentAsString).read("$.id").toString();
    var orderPayment = entityManager.find(Payment.class, UUID.fromString(id));
    assertThat(orderPayment).isNotNull();
    assertThat(orderPayment.getId()).isNotNull().isEqualTo(payment.getId());
    assertThat(orderPayment.getOrderId()).isNotNull().isEqualTo(payment.getOrderId());
  }
}
