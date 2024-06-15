package br.com.fiap.order.presentation.api;

import static br.com.fiap.order.domain.enums.OrderStatus.AGUARDANDO_ENTREGA;
import static br.com.fiap.order.domain.enums.OrderStatus.PAGO;
import static br.com.fiap.order.shared.testdata.OrderTestData.createNewOrder;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import br.com.fiap.order.domain.entity.Order;
import br.com.fiap.order.shared.annotation.DatabaseTest;
import br.com.fiap.order.shared.annotation.IntegrationTest;
import br.com.fiap.order.shared.api.JsonUtil;
import br.com.fiap.order.shared.testdata.OrderTestData;
import jakarta.persistence.EntityManager;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.MockMvc;

@IntegrationTest
@DatabaseTest
class PatchOrderAwaitingDeliveryApiTest {

  private static final String URL_ORDERS_AWAITING_DELIVERY = "/orders/{id}/awaiting-delivery";
  private final MockMvc mockMvc;
  private final EntityManager entityManager;

  @Autowired
  PatchOrderAwaitingDeliveryApiTest(MockMvc mockMvc, EntityManager entityManager) {
    this.mockMvc = mockMvc;
    this.entityManager = entityManager;
  }

  private Order createAndPersistOrder() {
    var order = createNewOrder();
    return entityManager.merge(order);
  }

  private Order getOrderById(UUID id) {
    return entityManager.find(Order.class, id);
  }

  @Test
  void shouldReturnNoContentWhenSetOrderStatusWithAwaitingDelivery() throws Exception {
    var order = createAndPersistOrder();
    order.setOrderStatus(PAGO);
    var orderInputDto = JsonUtil.toJson(order);

    var request = put(URL_ORDERS_AWAITING_DELIVERY, order.getId())
        .contentType(APPLICATION_JSON)
        .content(orderInputDto);
    mockMvc.perform(request)
        .andExpect(status().isNoContent());

    var orderFound = getOrderById(order.getId());
    assertThat(orderFound).isNotNull();
    assertThat(orderFound.getId()).isNotNull().isEqualTo(order.getId());
    assertThat(orderFound.getOrderStatus()).isNotNull().isEqualTo(AGUARDANDO_ENTREGA);
  }

  @Test
  void shouldReturnBadRequestWhenOrderIdIsInvalid() throws Exception {
    var order = OrderTestData.createOrder();
    var orderInputDto = JsonUtil.toJson(order);
    var orderId = "1aB";
    var request = put(URL_ORDERS_AWAITING_DELIVERY, orderId)
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
    var request = put(URL_ORDERS_AWAITING_DELIVERY, orderId)
        .contentType(APPLICATION_JSON)
        .content(orderInputDto);
    mockMvc.perform(request)
        .andExpect(status().isNotFound());
  }

  @Test
  void shouldReturnBadRequestWhenOrderIsAlreadyAwaitingDelivery() throws Exception {
    var order = createNewOrder();
    order.setOrderStatus(AGUARDANDO_ENTREGA);
    var orderPaymentConfirmation = entityManager.merge(order);
    var orderInputDto = JsonUtil.toJson(orderPaymentConfirmation);

    var request = put(URL_ORDERS_AWAITING_DELIVERY, orderPaymentConfirmation.getId())
        .contentType(APPLICATION_JSON)
        .content(orderInputDto);
    mockMvc.perform(request)
        .andExpect(status().isBadRequest());
  }

  @Test
  void shouldReturnBadRequestWhenOrderWasNotPaidToAwaitingDeliver() throws Exception {
    var order = createNewOrder();
    order.setOrderStatus(AGUARDANDO_ENTREGA);
    var orderPaymentConfirmation = entityManager.merge(order);
    var orderInputDto = JsonUtil.toJson(orderPaymentConfirmation);

    var request = put(URL_ORDERS_AWAITING_DELIVERY, orderPaymentConfirmation.getId())
        .contentType(APPLICATION_JSON)
        .content(orderInputDto);
    mockMvc.perform(request)
        .andExpect(status().isBadRequest());
  }
}
