package br.com.fiap.order.presentation.api;

import static br.com.fiap.order.shared.testdata.OrderTestData.createNewOrder;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

import br.com.fiap.order.domain.entity.Order;
import br.com.fiap.order.shared.annotation.DatabaseTest;
import br.com.fiap.order.shared.annotation.IntegrationTest;
import jakarta.persistence.EntityManager;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

@IntegrationTest
@DatabaseTest
class GetOrderByIdApiTest {

  private static final String URL_ORDERS = "/orders/{id}";
  private final MockMvc mockMvc;
  private final EntityManager entityManager;

  @Autowired
  GetOrderByIdApiTest(MockMvc mockMvc, EntityManager entityManager) {
    this.mockMvc = mockMvc;
    this.entityManager = entityManager;
  }

  private Order createAndPersistOrder() {
    var order = createNewOrder();
    return entityManager.merge(order);
  }

  @Test
  void shouldReturnOkWhenOrderWasFoundById() throws Exception {
    var order = createAndPersistOrder();

    var request = get(URL_ORDERS, order.getId());
    mockMvc.perform(request)
        .andExpect(MockMvcResultMatchers.status().isOk())
        .andExpect(content().contentType(APPLICATION_JSON))
        .andExpect(jsonPath("$.id", equalTo(order.getId().toString())));
  }

  @ParameterizedTest
  @ValueSource(strings = {"1", "a", "1a#"})
  void shouldReturnBadRequestWhenOrderIdIsInvalid(String orderId) throws Exception {
    var request = get(URL_ORDERS, orderId);
    mockMvc.perform(request)
        .andExpect(MockMvcResultMatchers.status().isBadRequest())
        .andExpect(content().contentType(APPLICATION_JSON));
  }

  @Test
  void shouldReturnNotFoundWhenOrderIdDoesNotExist() throws Exception {
    var request = get(URL_ORDERS, UUID.randomUUID());
    mockMvc.perform(request)
        .andExpect(MockMvcResultMatchers.status().isNotFound())
        .andExpect(content().contentType(APPLICATION_JSON));
  }
}
