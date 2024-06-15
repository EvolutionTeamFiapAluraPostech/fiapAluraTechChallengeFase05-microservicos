package br.com.fiap.order.domain.service;

import static br.com.fiap.order.domain.fields.OrderFields.ORDER_ID_FIELD;
import static br.com.fiap.order.domain.messages.OrderMessages.ORDER_NOT_FOUND_WITH_ID;
import static br.com.fiap.order.shared.testdata.OrderTestData.createOrder;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.when;

import br.com.fiap.order.domain.entity.Order;
import br.com.fiap.order.domain.exception.NoResultException;
import br.com.fiap.order.infrastructure.repository.OrderRepository;
import br.com.fiap.order.shared.testdata.OrderTestData;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.validation.FieldError;

@ExtendWith(MockitoExtension.class)
class OrderServiceTest {

  public static final int PAGE_NUMBER = 0;
  public static final int PAGE_SIZE = 1;

  @Mock
  private OrderRepository orderRepository;
  @InjectMocks
  private OrderService orderService;

  @Test
  void shouldCreateOrder() {
    var order = OrderTestData.createNewOrder();
    var orderWithId = createOrder();
    when(orderRepository.save(any(Order.class))).thenReturn(orderWithId);

    var orderSaved = orderService.save(order);

    assertThat(orderSaved).isNotNull();
    assertThat(orderSaved.getId()).isNotNull().isEqualTo(orderWithId.getId());
  }

  @Test
  void shouldFindOrderById() {
    var orderWithId = createOrder();
    when(orderRepository.findById(orderWithId.getId())).thenReturn(Optional.of(orderWithId));

    var orderFound = orderService.findByIdRequired(orderWithId.getId());

    assertThat(orderFound).isNotNull();
    assertThat(orderFound.getId()).isNotNull().isEqualTo(orderWithId.getId());
  }

  @Test
  void shouldThrowExceptionWhenOrderWasNotFoundById() {
    var orderId = UUID.randomUUID();
    when(orderRepository.findById(orderId)).thenThrow(
        new NoResultException(new FieldError(this.getClass().getSimpleName(), ORDER_ID_FIELD,
            ORDER_NOT_FOUND_WITH_ID.formatted(orderId))));

    assertThatThrownBy(() -> orderService.findByIdRequired(orderId))
        .isInstanceOf(NoResultException.class)
        .hasMessage(ORDER_NOT_FOUND_WITH_ID.formatted(orderId));
  }

  @Test
  void shouldFindOrderByCompanyIdOrCustomerIdPageable() {
    var order = createOrder();
    var companyId = order.getCompanyId();
    var customerId = order.getCustomerId();
    var orders = List.of(order);
    var pageable = PageRequest.of(PAGE_NUMBER, PAGE_SIZE);
    var size = orders.size();
    var orderPage = new PageImpl<>(orders, pageable, size);
    when(orderRepository.findOrderByCompanyIdOrCustomerId(companyId, customerId,
        pageable)).thenReturn(orderPage);

    var orderPageFound = orderService.findOrderByCompanyIdOrCustomerId(companyId, customerId,
        pageable);

    assertThat(orderPageFound).isNotNull().isNotEmpty();
    assertThat(orderPageFound.getSize()).isEqualTo(PAGE_SIZE);
    assertThat(orderPageFound.getTotalPages()).isEqualTo(size);
    assertThat(orderPageFound.getTotalElements()).isEqualTo(size);
  }

  @Test
  void shouldFindOrderByCompanyIdPageable() {
    var order = createOrder();
    var companyId = order.getCompanyId();
    var orders = List.of(order);
    var pageable = PageRequest.of(PAGE_NUMBER, PAGE_SIZE);
    var size = orders.size();
    var orderPage = new PageImpl<>(orders, pageable, size);
    when(orderRepository.findOrderByCompanyIdOrCustomerId(companyId, null,
        pageable)).thenReturn(orderPage);

    var orderPageFound = orderService.findOrderByCompanyIdOrCustomerId(companyId, null,
        pageable);

    assertThat(orderPageFound).isNotNull().isNotEmpty();
    assertThat(orderPageFound.getSize()).isEqualTo(PAGE_SIZE);
    assertThat(orderPageFound.getTotalPages()).isEqualTo(size);
    assertThat(orderPageFound.getTotalElements()).isEqualTo(size);
  }

  @Test
  void shouldFindOrderByCustomerIdPageable() {
    var order = createOrder();
    var customerId = order.getCustomerId();
    var orders = List.of(order);
    var pageable = PageRequest.of(PAGE_NUMBER, PAGE_SIZE);
    var size = orders.size();
    var orderPage = new PageImpl<>(orders, pageable, size);
    when(orderRepository.findOrderByCompanyIdOrCustomerId(null, customerId,
        pageable)).thenReturn(orderPage);

    var orderPageFound = orderService.findOrderByCompanyIdOrCustomerId(null, customerId,
        pageable);

    assertThat(orderPageFound).isNotNull().isNotEmpty();
    assertThat(orderPageFound.getSize()).isEqualTo(PAGE_SIZE);
    assertThat(orderPageFound.getTotalPages()).isEqualTo(size);
    assertThat(orderPageFound.getTotalElements()).isEqualTo(size);
  }

  @Test
  void shouldFindNothingWhenOrderWasNotFound() {
    var order = createOrder();
    var companyId = order.getCompanyId();
    var customerId = order.getCustomerId();
    var orders = new ArrayList<Order>();
    var pageable = PageRequest.of(PAGE_NUMBER, PAGE_SIZE);
    var size = 0;
    var orderPage = new PageImpl<>(orders, pageable, size);
    when(orderRepository.findOrderByCompanyIdOrCustomerId(companyId, customerId, pageable))
        .thenReturn(orderPage);

    var orderPageFound = orderService.findOrderByCompanyIdOrCustomerId(companyId, customerId,
        pageable);

    assertThat(orderPageFound).isNotNull();
    assertThat(orderPageFound.getSize()).isEqualTo(PAGE_SIZE);
    assertThat(orderPageFound.getTotalPages()).isEqualTo(size);
    assertThat(orderPageFound.getTotalElements()).isEqualTo(size);
  }
}
