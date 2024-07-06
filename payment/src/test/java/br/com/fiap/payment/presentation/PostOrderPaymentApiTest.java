package br.com.fiap.payment.presentation;

import static br.com.fiap.payment.domain.enums.PaymentType.PIX;
import static br.com.fiap.payment.infrastructure.httpclient.order.enums.OrderStatus.AGUARDANDO_PAGAMENTO;
import static br.com.fiap.payment.infrastructure.httpclient.order.enums.OrderStatus.PAGO;
import static br.com.fiap.payment.shared.util.IsUUID.isUUID;
import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import br.com.fiap.payment.domain.entity.Payment;
import br.com.fiap.payment.shared.annotation.DatabaseTest;
import br.com.fiap.payment.shared.annotation.IntegrationTest;
import com.github.tomakehurst.wiremock.client.WireMock;
import com.github.tomakehurst.wiremock.junit5.WireMockTest;
import com.jayway.jsonpath.JsonPath;
import jakarta.persistence.EntityManager;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.test.web.servlet.MockMvc;

@IntegrationTest
@DatabaseTest
@WireMockTest(httpPort = 7070)
class PostOrderPaymentApiTest {

  private static final String URL_PAYMENTS = "/payments";
  private static final String PAYMENT_INPUT_DTO = "{\"orderId\": \"%s\",\"paymentType\": \"%s\"}";
  public static final String URL_ORDERS = "/orders";
  private static final String ORDER_RESPONSE_BODY_ERROR = "{\"field\": \"order.id\",\"message\": \"Order not found. ID %s\"}";
  private static final String ORDER_RESPONSE_BODY = "{\"id\":\"%s\",\"orderStatus\":\"%s\",\"companyId\":\"%s\",\"customerId\":\"%s\",\"orderItems\":[{\"id\":\"28d39df4-6935-4003-b013-ed2d331ff491\",\"productId\":\"cfa8315f-3f9a-4105-a2f2-f02a0a303b20\",\"quantity\":10.00,\"price\":315.00,\"totalAmount\":3150.00}]}";
  private static final String ORDER_WITHOUT_ITEM_RESPONSE_BODY = "{\"id\":\"%s\",\"orderStatus\":\"%s\",\"companyId\":\"%s\",\"customerId\":\"%s\",\"orderItems\":[{}]}";
  private static final String ORDER_WITHOUT_TOTAL_AMOUNT_RESPONSE_BODY = "{\"id\":\"%s\",\"orderStatus\":\"%s\",\"companyId\":\"%s\",\"customerId\":\"%s\",\"orderItems\":[{\"id\":\"28d39df4-6935-4003-b013-ed2d331ff491\",\"productId\":\"cfa8315f-3f9a-4105-a2f2-f02a0a303b20\",\"quantity\":10.00,\"price\":315.00,\"totalAmount\":0.00}]}";
  private static final String URL_USERS = "/users";
  private static final String USER_RESPONSE_BODY_ERROR = "{\"field\":\"id\",\"message\":\"Customer not found with ID. %s\"}";
  private static final String USERS_RESPONSE_BODY = "{\"id\":\"%s\",\"name\":\"Thomas Anderson\",\"email\":\"thomas.anderson@itcompany.com\",\"docNumberType\":\"CPF\",\"docNumber\":\"95962710088\"}";
  private static final String URL_COMPANIES = "/companies";
  private static final String COMPANY_RESPONSE_BODY_ERROR = "{\"field\":\"id\",\"message\":\"Company not found with ID. %s\"}";
  private static final String COMPANY_RESPONSE_BODY = "{\"id\":\"%s\",\"name\":\"IT Company\",\"email\":\"it@itcompany.com\",\"docNumber\":\"59059270000110\",\"docNumberType\":\"CNPJ\",\"street\":\"Alameda Rio Claro\",\"number\":\"190\",\"neighborhood\":\"Bela Vista\",\"city\":\"São Paulo\",\"state\":\"SP\",\"country\":\"Brasil\",\"postalCode\":\"01332010\",\"latitude\":-23.563880,\"longitude\":-46.652410}";
  public static final String HEADER_CONTENT_TYPE = "Content-Type";
  public static final String APPLICATION_JSON_CHARSET_UTF_8 = "application/json;charset=UTF-8";
  public static final UUID ORDER_ID = UUID.randomUUID();
  public static final UUID COMPANY_ID = UUID.randomUUID();
  public static final UUID CUSTOMER_ID = UUID.randomUUID();
  public static final String URL_ORDER_PAYMENT_CONFIRMATION = "/orders/%s/payment-confirmation";

  private final MockMvc mockMvc;
  private final EntityManager entityManager;

  @Autowired
  PostOrderPaymentApiTest(MockMvc mockMvc, EntityManager entityManager) {
    this.mockMvc = mockMvc;
    this.entityManager = entityManager;
  }

  private Payment getOrderPaymentBy(String id) {
    return entityManager.find(Payment.class, UUID.fromString(id));
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
  void shouldReturnNotFoundWhenOrderWasNotFoundByOrderId() throws Exception {
    var paymentInputDto = PAYMENT_INPUT_DTO.formatted(ORDER_ID, PIX.name());
    stubFor(WireMock.get(urlEqualTo(URL_ORDERS + "/" + ORDER_ID))
        .willReturn(aResponse()
            .withStatus(HttpStatus.NOT_FOUND.value())
            .withHeader(HEADER_CONTENT_TYPE, APPLICATION_JSON_CHARSET_UTF_8)
            .withBody(
                ORDER_RESPONSE_BODY_ERROR.formatted(ORDER_ID))
        ));

    var request = post(URL_PAYMENTS)
        .contentType(APPLICATION_JSON)
        .content(paymentInputDto);
    mockMvc.perform(request)
        .andExpect(status().isNotFound());
  }

  @Test
  void shouldThrowBadRequestWhenOrderStatusIsInvalid() throws Exception {
    var paymentInputDto = PAYMENT_INPUT_DTO.formatted(ORDER_ID, PIX.name());
    stubFor(WireMock.get(urlEqualTo(URL_ORDERS + "/" + ORDER_ID))
        .willReturn(aResponse()
            .withStatus(HttpStatus.OK.value())
            .withHeader(HEADER_CONTENT_TYPE, APPLICATION_JSON_CHARSET_UTF_8)
            .withBody(ORDER_RESPONSE_BODY.formatted(ORDER_ID, PAGO.name(), COMPANY_ID, CUSTOMER_ID))
        ));

    var request = post(URL_PAYMENTS)
        .contentType(APPLICATION_JSON)
        .content(paymentInputDto);
    mockMvc.perform(request)
        .andExpect(status().isBadRequest());
  }

  @Test
  void shouldReturnNotFoundWhenOrderCompanyWasNotFoundByOrderId() throws Exception {
    var paymentInputDto = PAYMENT_INPUT_DTO.formatted(ORDER_ID, PIX.name());
    stubFor(WireMock.get(urlEqualTo(URL_ORDERS + "/" + ORDER_ID))
        .willReturn(aResponse()
            .withStatus(HttpStatus.OK.value())
            .withHeader(HEADER_CONTENT_TYPE, APPLICATION_JSON_CHARSET_UTF_8)
            .withBody(
                ORDER_RESPONSE_BODY.formatted(ORDER_ID, AGUARDANDO_PAGAMENTO.name(), COMPANY_ID,
                    CUSTOMER_ID))
        ));
    stubFor(WireMock.get(urlEqualTo(URL_COMPANIES + "/" + COMPANY_ID))
        .willReturn(aResponse()
            .withStatus(HttpStatus.NOT_FOUND.value())
            .withHeader(HEADER_CONTENT_TYPE, APPLICATION_JSON_CHARSET_UTF_8)
            .withBody(COMPANY_RESPONSE_BODY_ERROR.formatted(COMPANY_ID))
        ));

    var request = post(URL_PAYMENTS)
        .contentType(APPLICATION_JSON)
        .content(paymentInputDto);
    mockMvc.perform(request)
        .andExpect(status().isNotFound());
  }

  @Test
  void shouldReturnNotFoundWhenOrderCustomerWasNotFoundByOrderId() throws Exception {
    var paymentInputDto = PAYMENT_INPUT_DTO.formatted(ORDER_ID, PIX.name());
    stubFor(WireMock.get(urlEqualTo(URL_ORDERS + "/" + ORDER_ID))
        .willReturn(aResponse()
            .withStatus(HttpStatus.OK.value())
            .withHeader(HEADER_CONTENT_TYPE, APPLICATION_JSON_CHARSET_UTF_8)
            .withBody(
                ORDER_RESPONSE_BODY.formatted(ORDER_ID, AGUARDANDO_PAGAMENTO.name(), COMPANY_ID,
                    CUSTOMER_ID))
        ));
    stubFor(WireMock.get(urlEqualTo(URL_COMPANIES + "/" + COMPANY_ID))
        .willReturn(aResponse()
            .withStatus(HttpStatus.OK.value())
            .withHeader(HEADER_CONTENT_TYPE, APPLICATION_JSON_CHARSET_UTF_8)
            .withBody(COMPANY_RESPONSE_BODY.formatted(COMPANY_ID))
        ));
    stubFor(WireMock.get(urlEqualTo(URL_USERS + "/" + CUSTOMER_ID))
        .willReturn(aResponse()
            .withStatus(HttpStatus.NOT_FOUND.value())
            .withHeader(HEADER_CONTENT_TYPE, APPLICATION_JSON_CHARSET_UTF_8)
            .withBody(USER_RESPONSE_BODY_ERROR.formatted(CUSTOMER_ID))
        ));

    var request = post(URL_PAYMENTS)
        .contentType(APPLICATION_JSON)
        .content(paymentInputDto);
    mockMvc.perform(request)
        .andExpect(status().isNotFound());
  }

  @Test
  void shouldReturnBadRequestWhenOrderDoesNotHaveOrderItems() throws Exception {
    var paymentInputDto = PAYMENT_INPUT_DTO.formatted(ORDER_ID, PIX.name());
    stubFor(WireMock.get(urlEqualTo(URL_ORDERS + "/" + ORDER_ID))
        .willReturn(aResponse()
            .withStatus(HttpStatus.OK.value())
            .withHeader(HEADER_CONTENT_TYPE, APPLICATION_JSON_CHARSET_UTF_8)
            .withBody(
                ORDER_WITHOUT_ITEM_RESPONSE_BODY.formatted(ORDER_ID, AGUARDANDO_PAGAMENTO.name(),
                    COMPANY_ID, CUSTOMER_ID))
        ));
    stubFor(WireMock.get(urlEqualTo(URL_COMPANIES + "/" + COMPANY_ID))
        .willReturn(aResponse()
            .withStatus(HttpStatus.OK.value())
            .withHeader(HEADER_CONTENT_TYPE, APPLICATION_JSON_CHARSET_UTF_8)
            .withBody(COMPANY_RESPONSE_BODY.formatted(COMPANY_ID))
        ));
    stubFor(WireMock.get(urlEqualTo(URL_USERS + "/" + CUSTOMER_ID))
        .willReturn(aResponse()
            .withStatus(HttpStatus.OK.value())
            .withHeader(HEADER_CONTENT_TYPE, APPLICATION_JSON_CHARSET_UTF_8)
            .withBody(USERS_RESPONSE_BODY.formatted(CUSTOMER_ID))
        ));

    var request = post(URL_PAYMENTS)
        .contentType(APPLICATION_JSON)
        .content(paymentInputDto);
    mockMvc.perform(request)
        .andExpect(status().isBadRequest());
  }

  @Test
  void shouldReturnBadRequestWhenOrderTotalAmountIsInvalid() throws Exception {
    var paymentInputDto = PAYMENT_INPUT_DTO.formatted(ORDER_ID, PIX.name());
    stubFor(WireMock.get(urlEqualTo(URL_ORDERS + "/" + ORDER_ID))
        .willReturn(aResponse()
            .withStatus(HttpStatus.OK.value())
            .withHeader(HEADER_CONTENT_TYPE, APPLICATION_JSON_CHARSET_UTF_8)
            .withBody(
                ORDER_WITHOUT_TOTAL_AMOUNT_RESPONSE_BODY.formatted(ORDER_ID,
                    AGUARDANDO_PAGAMENTO.name(),
                    COMPANY_ID, CUSTOMER_ID))
        ));
    stubFor(WireMock.get(urlEqualTo(URL_COMPANIES + "/" + COMPANY_ID))
        .willReturn(aResponse()
            .withStatus(HttpStatus.OK.value())
            .withHeader(HEADER_CONTENT_TYPE, APPLICATION_JSON_CHARSET_UTF_8)
            .withBody(COMPANY_RESPONSE_BODY.formatted(COMPANY_ID))
        ));
    stubFor(WireMock.get(urlEqualTo(URL_USERS + "/" + CUSTOMER_ID))
        .willReturn(aResponse()
            .withStatus(HttpStatus.OK.value())
            .withHeader(HEADER_CONTENT_TYPE, APPLICATION_JSON_CHARSET_UTF_8)
            .withBody(USERS_RESPONSE_BODY.formatted(CUSTOMER_ID))
        ));

    var request = post(URL_PAYMENTS)
        .contentType(APPLICATION_JSON)
        .content(paymentInputDto);
    mockMvc.perform(request)
        .andExpect(status().isBadRequest());
  }

  @ParameterizedTest
  @NullAndEmptySource
  @ValueSource(strings = {"dinheiro", "MONEY", "CREDIT_CARD", "DEBIT_CARD", "pix"})
  void shouldReturnBadRequestWhenPaymentTypeIsNullOrEmptyOrInvalid(String paymentType)
      throws Exception {
    var paymentInputDto = PAYMENT_INPUT_DTO.formatted(ORDER_ID, paymentType);

    var request = post(URL_PAYMENTS)
        .contentType(APPLICATION_JSON)
        .content(paymentInputDto);
    mockMvc.perform(request)
        .andExpect(status().isBadRequest());
  }

  @Test
  void shouldReturnCreatedWhenSaveOrderPayment() throws Exception {
    var paymentInputDto = PAYMENT_INPUT_DTO.formatted(ORDER_ID, PIX.name());
    stubFor(WireMock.get(urlEqualTo(URL_ORDERS + "/" + ORDER_ID))
        .willReturn(aResponse()
            .withStatus(HttpStatus.OK.value())
            .withHeader(HEADER_CONTENT_TYPE, APPLICATION_JSON_CHARSET_UTF_8)
            .withBody(
                ORDER_RESPONSE_BODY.formatted(ORDER_ID, AGUARDANDO_PAGAMENTO.name(),
                    COMPANY_ID, CUSTOMER_ID))
        ));
    stubFor(WireMock.get(urlEqualTo(URL_COMPANIES + "/" + COMPANY_ID))
        .willReturn(aResponse()
            .withStatus(HttpStatus.OK.value())
            .withHeader(HEADER_CONTENT_TYPE, APPLICATION_JSON_CHARSET_UTF_8)
            .withBody(COMPANY_RESPONSE_BODY.formatted(COMPANY_ID))
        ));
    stubFor(WireMock.get(urlEqualTo(URL_USERS + "/" + CUSTOMER_ID))
        .willReturn(aResponse()
            .withStatus(HttpStatus.OK.value())
            .withHeader(HEADER_CONTENT_TYPE, APPLICATION_JSON_CHARSET_UTF_8)
            .withBody(USERS_RESPONSE_BODY.formatted(CUSTOMER_ID))
        ));
    stubFor(WireMock.put(urlEqualTo(URL_ORDER_PAYMENT_CONFIRMATION.formatted(ORDER_ID)))
        .willReturn(aResponse()
            .withStatus(HttpStatus.NO_CONTENT.value())));

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
