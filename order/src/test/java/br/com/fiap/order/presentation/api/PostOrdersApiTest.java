package br.com.fiap.order.presentation.api;

import static br.com.fiap.order.shared.testdata.OrderTestData.DEFAULT_PRODUCT_PRICE;
import static br.com.fiap.order.shared.testdata.OrderTestData.DEFAULT_PRODUCT_QUANTITY;
import static br.com.fiap.order.shared.testdata.OrderTestData.DEFAULT_PRODUCT_UUID;
import static br.com.fiap.order.shared.testdata.OrderTestData.createNewOrder;
import static br.com.fiap.order.shared.util.IsUUID.isUUID;
import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import br.com.fiap.order.domain.entity.Order;
import br.com.fiap.order.domain.entity.OrderItem;
import br.com.fiap.order.shared.annotation.DatabaseTest;
import br.com.fiap.order.shared.annotation.IntegrationTest;
import br.com.fiap.order.shared.api.JsonUtil;
import com.github.tomakehurst.wiremock.client.WireMock;
import com.github.tomakehurst.wiremock.junit5.WireMockTest;
import com.jayway.jsonpath.JsonPath;
import jakarta.persistence.EntityManager;
import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.test.web.servlet.MockMvc;

@IntegrationTest
@DatabaseTest
@WireMockTest(httpPort = 7070)
class PostOrdersApiTest {

  private static final String URL_ORDERS = "/orders";
  private final MockMvc mockMvc;
  private final EntityManager entityManager;

  @Autowired
  PostOrdersApiTest(MockMvc mockMvc, EntityManager entityManager) {
    this.mockMvc = mockMvc;
    this.entityManager = entityManager;
  }

  private Order getOrderById(String id) {
    return entityManager.find(Order.class, UUID.fromString(id));
  }

  @Test
  void shouldReturnCreatedWhenSaveOrder() throws Exception {
    var order = createNewOrder();
    var orderInputDto = JsonUtil.toJson(order);
    stubFor(WireMock.get("/companies/" + order.getCompanyId().toString())
        .willReturn(aResponse().withStatus(HttpStatus.OK.value())));
    stubFor(WireMock.get("/users/" + order.getCustomerId().toString())
        .willReturn(aResponse().withStatus(HttpStatus.OK.value())));
    stubFor(WireMock.get("/products/" + order.getOrderItems().get(0).getProductId())
        .willReturn(aResponse().withStatus(HttpStatus.OK.value())));

    var request = post(URL_ORDERS)
        .contentType(APPLICATION_JSON)
        .content(orderInputDto);
    var mockMvcResult = mockMvc.perform(request)
        .andExpect(status().isCreated())
        .andExpect(content().contentType(APPLICATION_JSON))
        .andExpect(jsonPath("$.id", isUUID()))
        .andReturn();

    var contentAsString = mockMvcResult.getResponse().getContentAsString();
    var id = JsonPath.parse(contentAsString).read("$.id").toString();
    var orderFound = getOrderById(id);
    assertThat(orderFound).isNotNull();
    assertThat(orderFound.getId()).isNotNull().isEqualTo(UUID.fromString(id));
    assertThat(orderFound.getCompanyId()).isNotNull().isEqualTo(order.getCompanyId());
    assertThat(orderFound.getCustomerId()).isNotNull().isEqualTo(order.getCustomerId());
    assertThat(orderFound.getOrderStatus()).isEqualTo(order.getOrderStatus());
  }

  @Test
  void shouldReturnBadRequestWhenOrderCompanyIdWasNotFilled() throws Exception {
    var order = createNewOrder();
    order.setCompanyId(null);
    var orderInputDto = JsonUtil.toJson(order);

    var request = post(URL_ORDERS)
        .contentType(APPLICATION_JSON)
        .content(orderInputDto);
    mockMvc.perform(request)
        .andExpect(status().isBadRequest())
        .andExpect(content().contentType(APPLICATION_JSON));
  }

  @Test
  void shouldReturnNotFoundWhenOrderCompanyIdDoesNotExist() throws Exception {
    var order = createNewOrder();
    order.setCompanyId(UUID.randomUUID());
    var orderInputDto = JsonUtil.toJson(order);
    stubFor(WireMock.get("/companies/" + order.getCompanyId().toString())
        .willReturn(aResponse().withStatus(HttpStatus.NOT_FOUND.value())));

    var request = post(URL_ORDERS)
        .contentType(APPLICATION_JSON)
        .content(orderInputDto);
    mockMvc.perform(request)
        .andExpect(status().isNotFound())
        .andExpect(content().contentType(APPLICATION_JSON));
  }

  @Test
  void shouldReturnBadRequestWhenOrderCustomerIdWasNotFilled() throws Exception {
    var order = createNewOrder();
    order.setCustomerId(null);
    var orderInputDto = JsonUtil.toJson(order);

    var request = post(URL_ORDERS)
        .contentType(APPLICATION_JSON)
        .content(orderInputDto);
    mockMvc.perform(request)
        .andExpect(status().isBadRequest())
        .andExpect(content().contentType(APPLICATION_JSON));
  }

  @Test
  void shouldReturnNotFoundWhenOrderCustomerIdDoesNotExist() throws Exception {
    var order = createNewOrder();
    order.setCustomerId(UUID.randomUUID());
    var orderInputDto = JsonUtil.toJson(order);
    stubFor(WireMock.get("/companies/" + order.getCompanyId().toString())
        .willReturn(aResponse().withStatus(HttpStatus.OK.value())));
    stubFor(WireMock.get("/users/" + order.getCustomerId().toString())
        .willReturn(aResponse().withStatus(HttpStatus.NOT_FOUND.value())));

    var request = post(URL_ORDERS)
        .contentType(APPLICATION_JSON)
        .content(orderInputDto);
    mockMvc.perform(request)
        .andExpect(status().isNotFound())
        .andExpect(content().contentType(APPLICATION_JSON));
  }

  @Test
  void shouldReturnBadRequestWhenOrderItemsWasNotFilled() throws Exception {
    var order = createNewOrder();
    order.setOrderItems(Collections.emptyList());
    var orderInputDto = JsonUtil.toJson(order);

    var request = post(URL_ORDERS)
        .contentType(APPLICATION_JSON)
        .content(orderInputDto);
    mockMvc.perform(request)
        .andExpect(status().isBadRequest())
        .andExpect(content().contentType(APPLICATION_JSON));
  }

  @Test
  void shouldReturnBadRequestWhenOrderItemsProductIdWasNotFilled() throws Exception {
    var orderInputDto = """
        {
            "companyId": "dcd3398e-4988-4fba-b8c0-a649ae1ff677",
            "customerId": "64f6db0a-3d9a-429c-a7e6-04c4691f3be9",
            "orderItems": [
                {
                    "productId": "",
                    "quantity": 10,
                    "price": 315
                }
            ]
        }""";

    var request = post(URL_ORDERS)
        .contentType(APPLICATION_JSON)
        .content(orderInputDto);
    mockMvc.perform(request)
        .andExpect(status().isBadRequest())
        .andExpect(content().contentType(APPLICATION_JSON));
  }

  @Test
  void shouldReturnNotFoundWhenOrderItemProductIdIdDoesNotExist() throws Exception {
    var order = createNewOrder();
    order.getOrderItems().get(0).setProductId(UUID.randomUUID());
    var orderInputDto = JsonUtil.toJson(order);
    stubFor(WireMock.get("/companies/" + order.getCompanyId().toString())
        .willReturn(aResponse().withStatus(HttpStatus.OK.value())));
    stubFor(WireMock.get("/users/" + order.getCustomerId().toString())
        .willReturn(aResponse().withStatus(HttpStatus.OK.value())));
    stubFor(WireMock.get("/products/" + order.getOrderItems().get(0).getProductId())
        .willReturn(aResponse().withStatus(HttpStatus.NOT_FOUND.value())));

    var request = post(URL_ORDERS)
        .contentType(APPLICATION_JSON)
        .content(orderInputDto);
    mockMvc.perform(request)
        .andExpect(status().isNotFound())
        .andExpect(content().contentType(APPLICATION_JSON));
  }

  @Test
  void shouldReturnBadRequestWhenOrderItemsQuantityIsZero() throws Exception {
    var order = createNewOrder();
    var orderItem = OrderItem.builder()
        .productId(DEFAULT_PRODUCT_UUID)
        .quantity(BigDecimal.ZERO)
        .price(DEFAULT_PRODUCT_PRICE)
        .build();
    var orderItems = List.of(orderItem);
    order.setOrderItems(orderItems);
    var orderInputDto = JsonUtil.toJson(order);

    var request = post(URL_ORDERS)
        .contentType(APPLICATION_JSON)
        .content(orderInputDto);
    mockMvc.perform(request)
        .andExpect(status().isBadRequest())
        .andExpect(content().contentType(APPLICATION_JSON));
  }

  @Test
  void shouldReturnBadRequestWhenOrderItemsPriceIsZero() throws Exception {
    var order = createNewOrder();
    var orderItem = OrderItem.builder()
        .productId(DEFAULT_PRODUCT_UUID)
        .quantity(DEFAULT_PRODUCT_QUANTITY)
        .price(BigDecimal.ZERO)
        .build();
    var orderItems = List.of(orderItem);
    order.setOrderItems(orderItems);
    var orderInputDto = JsonUtil.toJson(order);

    var request = post(URL_ORDERS)
        .contentType(APPLICATION_JSON)
        .content(orderInputDto);
    mockMvc.perform(request)
        .andExpect(status().isBadRequest())
        .andExpect(content().contentType(APPLICATION_JSON));
  }
}
