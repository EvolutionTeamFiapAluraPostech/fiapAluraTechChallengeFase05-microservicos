package br.com.fiap.order.presentation.api;

import static br.com.fiap.order.shared.testdata.OrderTestData.ALTERNATIVE_COMPANY_UUID;
import static br.com.fiap.order.shared.testdata.OrderTestData.ALTERNATIVE_CUSTOMER_UUID;
import static br.com.fiap.order.shared.testdata.OrderTestData.ALTERNATIVE_PRODUCT_PRICE;
import static br.com.fiap.order.shared.testdata.OrderTestData.ALTERNATIVE_PRODUCT_QUANTITY;
import static br.com.fiap.order.shared.testdata.OrderTestData.ALTERNATIVE_PRODUCT_UUID;
import static br.com.fiap.order.shared.testdata.OrderTestData.DEFAULT_PRODUCT_PRICE;
import static br.com.fiap.order.shared.testdata.OrderTestData.DEFAULT_PRODUCT_QUANTITY;
import static br.com.fiap.order.shared.testdata.OrderTestData.OTHER_PRODUCT_UUID;
import static br.com.fiap.order.shared.testdata.OrderTestData.createNewOrder;
import static br.com.fiap.order.shared.util.IsUUID.isUUID;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import br.com.fiap.order.domain.entity.Order;
import br.com.fiap.order.domain.entity.OrderItem;
import br.com.fiap.order.presentation.api.dto.OrderDto;
import br.com.fiap.order.shared.annotation.DatabaseTest;
import br.com.fiap.order.shared.annotation.IntegrationTest;
import br.com.fiap.order.shared.api.JsonUtil;
import br.com.fiap.order.shared.testdata.OrderTestData;
import com.jayway.jsonpath.JsonPath;
import jakarta.persistence.EntityManager;
import java.math.BigDecimal;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.MockMvc;

@IntegrationTest
@DatabaseTest
class PutOrderApiTest {

  private static final String URL_ORDERS = "/orders/{id}";
  private final MockMvc mockMvc;
  private final EntityManager entityManager;

  @Autowired
  PutOrderApiTest(MockMvc mockMvc, EntityManager entityManager) {
    this.mockMvc = mockMvc;
    this.entityManager = entityManager;
  }

  private Order createAndPersistOrder() {
    var order = createNewOrder();
    return entityManager.merge(order);
  }

  private Order getOrderById(String id) {
    return entityManager.find(Order.class, UUID.fromString(id));
  }

  @Test
  void shouldReturnAcceptedWhenUpdateOrder() throws Exception {
    var order = createAndPersistOrder();
    order.setCustomerId(UUID.randomUUID());
    var orderInputDto = JsonUtil.toJson(order);

    var request = put(URL_ORDERS, order.getId())
        .contentType(APPLICATION_JSON)
        .content(orderInputDto);
    var mockMvcRequest = mockMvc.perform(request)
        .andExpect(status().isCreated())
        .andExpect(content().contentType(APPLICATION_JSON))
        .andExpect(jsonPath("$.id", isUUID()))
        .andReturn();

    var contentAsString = mockMvcRequest.getResponse().getContentAsString();
    var id = JsonPath.parse(contentAsString).read("$.id").toString();
    var orderFound = getOrderById(id);
    assertThat(orderFound).isNotNull();
    assertThat(orderFound.getId()).isNotNull().isEqualTo(UUID.fromString(id));
    assertThat(orderFound.getCustomerId()).isNotNull().isEqualTo(order.getCustomerId());
  }

  @Test
  void shouldReturnAcceptedWhenUpdateOrderWithTwoItems() throws Exception {
    var order = createAndPersistOrder();
    var secondOrderItem = OrderItem.builder()
        .order(order)
        .productId(OTHER_PRODUCT_UUID)
        .quantity(DEFAULT_PRODUCT_QUANTITY)
        .price(DEFAULT_PRODUCT_PRICE)
        .build();
    order.getOrderItems().add(secondOrderItem);
    order.setCompanyId(ALTERNATIVE_COMPANY_UUID);
    order.setCustomerId(ALTERNATIVE_CUSTOMER_UUID);
    order.getOrderItems().get(0).setProductId(ALTERNATIVE_PRODUCT_UUID);
    order.getOrderItems().get(0).setQuantity(ALTERNATIVE_PRODUCT_QUANTITY);
    order.getOrderItems().get(0).setPrice(ALTERNATIVE_PRODUCT_PRICE);
    var orderDto = new OrderDto(order);
    var orderInputDto = JsonUtil.toJson(orderDto);

    var request = put(URL_ORDERS, order.getId())
        .contentType(APPLICATION_JSON)
        .content(orderInputDto);
    var mockMvcRequest = mockMvc.perform(request)
        .andExpect(status().isCreated())
        .andExpect(content().contentType(APPLICATION_JSON))
        .andExpect(jsonPath("$.id", isUUID()))
        .andReturn();

    var contentAsString = mockMvcRequest.getResponse().getContentAsString();
    var id = JsonPath.parse(contentAsString).read("$.id").toString();
    var orderFound = getOrderById(id);
    assertThat(orderFound).isNotNull();
    assertThat(orderFound.getId()).isNotNull().isEqualTo(UUID.fromString(id));
    assertThat(orderFound.getCustomerId()).isNotNull().isEqualTo(order.getCustomerId());
    assertThat(orderFound.getOrderItems().get(0).getId()).isNotNull()
        .isEqualTo(order.getOrderItems().get(0).getId());
    assertThat(orderFound.getOrderItems().get(0).getProductId()).isNotNull()
        .isEqualTo(order.getOrderItems().get(0).getProductId());
    assertThat(orderFound.getOrderItems().get(0).getQuantity()).isNotNull()
        .isEqualTo(order.getOrderItems().get(0).getQuantity());
    assertThat(orderFound.getOrderItems().get(0).getPrice()).isNotNull()
        .isEqualTo(order.getOrderItems().get(0).getPrice());
    assertThat(orderFound.getOrderItems().get(1).getId()).isNotNull();
    assertThat(orderFound.getOrderItems().get(1).getProductId()).isNotNull()
        .isEqualTo(order.getOrderItems().get(1).getProductId());
    assertThat(orderFound.getOrderItems().get(1).getQuantity()).isNotNull()
        .isEqualTo(order.getOrderItems().get(1).getQuantity());
    assertThat(orderFound.getOrderItems().get(1).getPrice()).isNotNull()
        .isEqualTo(order.getOrderItems().get(1).getPrice());
  }

  @Test
  void shouldReturnBadRequestWhenOrderIdIsInvalid() throws Exception {
    var order = OrderTestData.createOrder();
    var orderInputDto = JsonUtil.toJson(order);
    var orderId = "1aB";
    var request = put(URL_ORDERS, orderId)
        .contentType(APPLICATION_JSON)
        .content(orderInputDto);
    mockMvc.perform(request)
        .andExpect(status().isBadRequest());
  }

  @Test
  void shouldReturnNotFoundWhenOrderWasNotFound() throws Exception {
    var order = OrderTestData.createOrder();
    var orderInputDto = JsonUtil.toJson(order);
    var orderId = UUID.randomUUID();
    var request = put(URL_ORDERS, orderId)
        .contentType(APPLICATION_JSON)
        .content(orderInputDto);
    mockMvc.perform(request)
        .andExpect(status().isNotFound());
  }

  @Test
  void shouldReturnBadRequestWhenOrderCompanyIdWasNotFilled() throws Exception {
    var order = createAndPersistOrder();
    order.setCompanyId(null);
    var orderInputDto = JsonUtil.toJson(order);

    var request = put(URL_ORDERS, order.getId())
        .contentType(APPLICATION_JSON)
        .content(orderInputDto);
    mockMvc.perform(request)
        .andExpect(status().isBadRequest())
        .andExpect(content().contentType(APPLICATION_JSON));
  }

  @Test
  void shouldReturnBadRequestWhenOrderCustomerIdWasNotFilled() throws Exception {
    var order = createAndPersistOrder();
    order.setCustomerId(null);
    var orderInputDto = JsonUtil.toJson(order);

    var request = put(URL_ORDERS, order.getId())
        .contentType(APPLICATION_JSON)
        .content(orderInputDto);
    mockMvc.perform(request)
        .andExpect(status().isBadRequest())
        .andExpect(content().contentType(APPLICATION_JSON));
  }

  @Test
  void shouldReturnBadRequestWhenOrderItemsWasNotFilled() throws Exception {
    var order = createAndPersistOrder();
    order.setOrderItems(null);
    var orderInputDto = JsonUtil.toJson(order);

    var request = put(URL_ORDERS, order.getId())
        .contentType(APPLICATION_JSON)
        .content(orderInputDto);
    mockMvc.perform(request)
        .andExpect(status().isBadRequest())
        .andExpect(content().contentType(APPLICATION_JSON));
  }

  @Test
  void shouldReturnBadRequestWhenOrderItemsProductIdWasNotFilled() throws Exception {
    var order = createAndPersistOrder();
    order.getOrderItems().forEach(orderItem -> orderItem.setProductId(null));
    var orderInputDto = JsonUtil.toJson(order);

    var request = put(URL_ORDERS, order.getId())
        .contentType(APPLICATION_JSON)
        .content(orderInputDto);
    mockMvc.perform(request)
        .andExpect(status().isBadRequest())
        .andExpect(content().contentType(APPLICATION_JSON));
  }

  @Test
  void shouldReturnBadRequestWhenOrderItemsQuantityIsZero() throws Exception {
    var order = createAndPersistOrder();
    order.getOrderItems().forEach(orderItem -> orderItem.setQuantity(BigDecimal.ZERO));
    var orderInputDto = JsonUtil.toJson(order);

    var request = put(URL_ORDERS, order.getId())
        .contentType(APPLICATION_JSON)
        .content(orderInputDto);
    mockMvc.perform(request)
        .andExpect(status().isBadRequest())
        .andExpect(content().contentType(APPLICATION_JSON));
  }

  @Test
  void shouldReturnBadRequestWhenOrderItemsPriceIsZero() throws Exception {
    var order = createAndPersistOrder();
    order.getOrderItems().forEach(orderItem -> orderItem.setPrice(BigDecimal.ZERO));
    var orderInputDto = JsonUtil.toJson(order);

    var request = put(URL_ORDERS, order.getId())
        .contentType(APPLICATION_JSON)
        .content(orderInputDto);
    mockMvc.perform(request)
        .andExpect(status().isBadRequest())
        .andExpect(content().contentType(APPLICATION_JSON));
  }
}
