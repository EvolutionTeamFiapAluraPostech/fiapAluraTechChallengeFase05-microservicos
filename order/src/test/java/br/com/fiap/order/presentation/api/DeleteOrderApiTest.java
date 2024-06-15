package br.com.fiap.order.presentation.api;

import static br.com.fiap.order.shared.testdata.OrderTestData.createNewOrder;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import br.com.fiap.order.domain.entity.Order;
import br.com.fiap.order.shared.annotation.DatabaseTest;
import br.com.fiap.order.shared.annotation.IntegrationTest;
import jakarta.persistence.EntityManager;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.MockMvc;

@IntegrationTest
@DatabaseTest
class DeleteOrderApiTest {

  private static final String URL_ORDERS = "/orders/{id}";
  private final MockMvc mockMvc;
  private final EntityManager entityManager;

  @Autowired
  DeleteOrderApiTest(MockMvc mockMvc, EntityManager entityManager) {
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
  void shouldReturnNoContentWhenDeleteOrder() throws Exception {
    var order = createAndPersistOrder();

    var request = delete(URL_ORDERS, order.getId());
    mockMvc.perform(request).andExpect(status().isNoContent());

    var deletedOrder = getOrderById(order.getId().toString());
    assertThat(deletedOrder).isNotNull();
    assertThat(deletedOrder.getId()).isNotNull().isEqualTo(order.getId());
    assertThat(deletedOrder.getDeleted()).isNotNull().isTrue();
    assertThat(deletedOrder.getOrderItems()).isNotEmpty();
    assertThat(deletedOrder.getOrderItems().get(0).getDeleted()).isTrue();
  }

  @Test
  void shouldReturnBadRequestWhenDeleteOrderWithInvalidId() throws Exception {
    var request = delete(URL_ORDERS, "1aB");
    mockMvc.perform(request)
        .andExpect(status().isBadRequest());
  }

  @Test
  void shouldReturnNotFoundWhenProductDoesNotExist() throws Exception {
    var request = delete(URL_ORDERS, UUID.randomUUID());
    mockMvc.perform(request)
        .andExpect(status().isNotFound());
  }
}
