package br.com.fiap.order.presentation.api;

import static br.com.fiap.order.shared.testdata.OrderTestData.createNewOrder;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import br.com.fiap.order.domain.entity.Order;
import br.com.fiap.order.presentation.api.dto.OrderContent;
import br.com.fiap.order.presentation.api.dto.OrderDto;
import br.com.fiap.order.shared.annotation.DatabaseTest;
import br.com.fiap.order.shared.annotation.IntegrationTest;
import br.com.fiap.order.shared.api.JsonUtil;
import br.com.fiap.order.shared.api.PageUtil;
import jakarta.persistence.EntityManager;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.MockMvc;

@IntegrationTest
@DatabaseTest
class GetOrdersByCompanyIdAndCustomerIdApiTest {

  private static final String URL_ORDERS = "/orders/company-customer";
  private final MockMvc mockMvc;
  private final EntityManager entityManager;

  @Autowired
  GetOrdersByCompanyIdAndCustomerIdApiTest(MockMvc mockMvc, EntityManager entityManager) {
    this.mockMvc = mockMvc;
    this.entityManager = entityManager;
  }

  private Order createAndPersistOrder() {
    var order = createNewOrder();
    return entityManager.merge(order);
  }

  @Test
  void shouldReturnOkWhenGetOrdersByCompanyIdAndCustomerId() throws Exception {
    var order = createAndPersistOrder();
    var ordersPage = PageUtil.generatePageOfOrder(order);
    var orderOutputDtoExpected = OrderDto.toPage(ordersPage);

    var request = get(URL_ORDERS)
        .param("companyId", order.getCompanyId().toString())
        .param("customerId", order.getCustomerId().toString());
    var mockMvcResult = mockMvc.perform(request)
        .andExpect(status().isOk())
        .andExpect(content().contentType(APPLICATION_JSON))
        .andReturn();

    var contentAsString = mockMvcResult.getResponse().getContentAsString();
    var ordersPageFound = JsonUtil.fromJson(contentAsString, OrderContent.class);
    assertThat(ordersPageFound).isNotNull();
    assertThat(ordersPageFound.getContent()).isNotEmpty();
    assertThat(ordersPageFound.getContent().get(0)).isNotNull();
    assertThat(ordersPageFound.getContent().get(0).id()).isNotNull().isNotEmpty();
    assertThat(ordersPageFound.getContent().get(0).id()).isEqualTo(
        orderOutputDtoExpected.getContent().get(0).id());
  }

  @Test
  void shouldReturnOkWhenGetOrdersByCompanyId() throws Exception {
    var order = createAndPersistOrder();
    var ordersPage = PageUtil.generatePageOfOrder(order);
    var orderOutputDtoExpected = OrderDto.toPage(ordersPage);

    var request = get(URL_ORDERS)
        .param("companyId", order.getCompanyId().toString());
    var mockMvcResult = mockMvc.perform(request)
        .andExpect(status().isOk())
        .andExpect(content().contentType(APPLICATION_JSON))
        .andReturn();

    var contentAsString = mockMvcResult.getResponse().getContentAsString();
    var ordersPageFound = JsonUtil.fromJson(contentAsString, OrderContent.class);
    assertThat(ordersPageFound).isNotNull();
    assertThat(ordersPageFound.getContent()).isNotEmpty();
    assertThat(ordersPageFound.getContent().get(0)).isNotNull();
    assertThat(ordersPageFound.getContent().get(0).id()).isNotNull().isNotEmpty();
    assertThat(ordersPageFound.getContent().get(0).id()).isEqualTo(
        orderOutputDtoExpected.getContent().get(0).id());
  }

  @Test
  void shouldReturnOkWhenGetOrdersByCustomerId() throws Exception {
    var order = createAndPersistOrder();
    var ordersPage = PageUtil.generatePageOfOrder(order);
    var orderOutputDtoExpected = OrderDto.toPage(ordersPage);

    var request = get(URL_ORDERS)
        .param("customerId", order.getCustomerId().toString());
    var mockMvcResult = mockMvc.perform(request)
        .andExpect(status().isOk())
        .andExpect(content().contentType(APPLICATION_JSON))
        .andReturn();

    var contentAsString = mockMvcResult.getResponse().getContentAsString();
    var ordersPageFound = JsonUtil.fromJson(contentAsString, OrderContent.class);
    assertThat(ordersPageFound).isNotNull();
    assertThat(ordersPageFound.getContent()).isNotEmpty();
    assertThat(ordersPageFound.getContent().get(0)).isNotNull();
    assertThat(ordersPageFound.getContent().get(0).id()).isNotNull().isNotEmpty();
    assertThat(ordersPageFound.getContent().get(0).id()).isEqualTo(
        orderOutputDtoExpected.getContent().get(0).id());
  }

  @Test
  void shouldReturnOkWhenGetAllOrdersButNothingWasFound() throws Exception {
    var request = get(URL_ORDERS)
        .param("companyId", UUID.randomUUID().toString())
        .param("customerId", UUID.randomUUID().toString());
    var mockMvcResult = mockMvc.perform(request)
        .andExpect(status().isOk())
        .andExpect(content().contentType(APPLICATION_JSON))
        .andReturn();

    var contentAsString = mockMvcResult.getResponse().getContentAsString();
    var ordersPageFound = JsonUtil.fromJson(contentAsString, OrderContent.class);
    assertThat(ordersPageFound).isNotNull();
    assertThat(ordersPageFound.getContent()).isEmpty();
  }
}
