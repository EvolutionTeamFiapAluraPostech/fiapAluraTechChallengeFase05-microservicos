package br.com.fiap.payment.presentation;

import static br.com.fiap.payment.shared.util.IsUUID.isUUID;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import br.com.fiap.payment.domain.entity.Payment;
import br.com.fiap.payment.shared.annotation.DatabaseTest;
import br.com.fiap.payment.shared.annotation.IntegrationTest;
import com.github.tomakehurst.wiremock.junit5.WireMockTest;
import com.jayway.jsonpath.JsonPath;
import jakarta.persistence.EntityManager;
import java.util.UUID;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.MockMvc;

@IntegrationTest
@DatabaseTest
@WireMockTest(httpPort = 7070)
class PostOrderPaymentApiTest {

  private static final String URL_PAYMENTS = "/payments";
  private static final String PAYMENT_INPUT_DTO = """
      {
          "orderId": %s,
          "paymentType": "%s"
      }""";

  private final MockMvc mockMvc;
  private final EntityManager entityManager;

  @Autowired
  PostOrderPaymentApiTest(MockMvc mockMvc, EntityManager entityManager) {
    this.mockMvc = mockMvc;
    this.entityManager = entityManager;
  }

  private Payment getOrderPaymentBy(String id) {
    return entityManager.find(Payment.class, id);
  }

  @ParameterizedTest
  @NullAndEmptySource
  @ValueSource(strings = "aB1")
  void shouldReturnBadRequestWhenOrderIdIsNullOrEmptyOrInvalid(String orderId) throws Exception {
    var paymentType = "PIX";
    var paymentInputDto = PAYMENT_INPUT_DTO.formatted(orderId, paymentType);

    var request = post(URL_PAYMENTS)
        .contentType(APPLICATION_JSON)
        .content(paymentInputDto);
    mockMvc.perform(request)
        .andExpect(status().isBadRequest());
  }

  @Test
  void shouldReturnNotFoundWhenOrderWasNotFoundByOrderId() {
    Assertions.fail("shouldReturnNotFoundWhenOrderWasNotFoundByOrderId");
  }

  @ParameterizedTest
  @NullAndEmptySource
  @ValueSource(strings = {"dinheiro", "MONEY", "CREDIT_CARD", "DEBIT_CARD", "pix"})
  void shouldReturnBadRequestWhenPaymentTypeIsNullOrEmptyOrInvalid(String paymentType) {
    Assertions.fail("shouldReturnBadRequestWhenPaymentTypeIsNullOrEmptyOrInvalid");
  }

  @Test
  void shouldReturnCreatedWhenSaveOrderPayment() throws Exception {
    var orderId = UUID.randomUUID();
    var paymentType = "PIX";
    var paymentInputDto = PAYMENT_INPUT_DTO.formatted(orderId, paymentType);

    var request = post(URL_PAYMENTS)
        .contentType(APPLICATION_JSON)
        .content(paymentInputDto);
    var mockMvcResult = mockMvc.perform(request)
        .andExpect(status().isCreated())
        .andExpect(content().contentType(APPLICATION_JSON))
        .andExpect(jsonPath("$.id", isUUID()))
        .andReturn();

    var contentAsString = mockMvcResult.getResponse().getContentAsString();
    var id = JsonPath.parse(contentAsString).read("$.id").toString();
    var orderPayment = getOrderPaymentBy(id);
    assertThat(orderPayment).isNotNull();
    assertThat(orderPayment.getId()).isNotNull();
  }
}
