package br.com.fiap.order.application.usecase;

import static br.com.fiap.order.shared.testdata.OrderTestData.createOrder;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import br.com.fiap.order.application.validator.UuidValidator;
import br.com.fiap.order.domain.entity.Order;
import br.com.fiap.order.domain.service.OrderService;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

@ExtendWith(MockitoExtension.class)
class GetOrdersByCompanyIdOrCustomerIdUseCaseTest {

  public static final int PAGE_NUMBER = 0;
  public static final int PAGE_SIZE = 1;

  @Mock
  private OrderService orderService;
  @Mock
  private UuidValidator uuidValidator;
  @InjectMocks
  private GetOrdersByCompanyIdOrCustomerIdUseCase getOrdersByCompanyIdOrCustomerIdUseCase;

  @Test
  void shouldGetOrdersByCompanyIdAndCustomerId() {
    var order = createOrder();
    var companyId = order.getCompanyId();
    var customerId = order.getCustomerId();
    var orders = List.of(order);
    var pageable = PageRequest.of(PAGE_NUMBER, PAGE_SIZE);
    var size = orders.size();
    var orderPage = new PageImpl<>(orders, pageable, size);
    when(orderService.findOrderByCompanyIdOrCustomerId(companyId, customerId, pageable))
        .thenReturn(orderPage);

    var orderPageFound = getOrdersByCompanyIdOrCustomerIdUseCase.execute(companyId.toString(),
        customerId.toString(), pageable);

    assertThat(orderPageFound).isNotNull().isNotEmpty();
    assertThat(orderPageFound.getSize()).isEqualTo(PAGE_SIZE);
    assertThat(orderPageFound.getTotalPages()).isEqualTo(size);
    assertThat(orderPageFound.getTotalElements()).isEqualTo(size);
    verify(uuidValidator, times(2)).validate(any(String.class));
  }

  @Test
  void shouldGetOrdersByCompanyId() {
    var order = createOrder();
    var companyId = order.getCompanyId();
    var orders = List.of(order);
    var pageable = PageRequest.of(PAGE_NUMBER, PAGE_SIZE);
    var size = orders.size();
    var orderPage = new PageImpl<>(orders, pageable, size);
    when(orderService.findOrderByCompanyIdOrCustomerId(companyId, null, pageable))
        .thenReturn(orderPage);

    var orderPageFound = getOrdersByCompanyIdOrCustomerIdUseCase.execute(companyId.toString(),
        null, pageable);

    assertThat(orderPageFound).isNotNull().isNotEmpty();
    assertThat(orderPageFound.getSize()).isEqualTo(PAGE_SIZE);
    assertThat(orderPageFound.getTotalPages()).isEqualTo(size);
    assertThat(orderPageFound.getTotalElements()).isEqualTo(size);
    verify(uuidValidator, times(1)).validate(any(String.class));
  }

  @Test
  void shouldGetOrdersByCustomerId() {
    var order = createOrder();
    var customerId = order.getCustomerId();
    var orders = List.of(order);
    var pageable = PageRequest.of(PAGE_NUMBER, PAGE_SIZE);
    var size = orders.size();
    var orderPage = new PageImpl<>(orders, pageable, size);
    when(orderService.findOrderByCompanyIdOrCustomerId(null, customerId, pageable))
        .thenReturn(orderPage);

    var orderPageFound = getOrdersByCompanyIdOrCustomerIdUseCase.execute(null,
        customerId.toString(), pageable);

    assertThat(orderPageFound).isNotNull().isNotEmpty();
    assertThat(orderPageFound.getSize()).isEqualTo(PAGE_SIZE);
    assertThat(orderPageFound.getTotalPages()).isEqualTo(size);
    assertThat(orderPageFound.getTotalElements()).isEqualTo(size);
    verify(uuidValidator, times(1)).validate(any(String.class));
  }

  @Test
  void shouldReturnNothingWhenGetOrdersByCompanyIdAndCustomerIdWasNotFound() {
    var order = createOrder();
    var companyId = order.getCompanyId();
    var customerId = order.getCustomerId();
    var orders = new ArrayList<Order>();
    var pageable = PageRequest.of(PAGE_NUMBER, PAGE_SIZE);
    var size = 0;
    var orderPage = new PageImpl<>(orders, pageable, size);
    when(orderService.findOrderByCompanyIdOrCustomerId(companyId, customerId, pageable))
        .thenReturn(orderPage);

    var orderPageFound = getOrdersByCompanyIdOrCustomerIdUseCase.execute(companyId.toString(),
        customerId.toString(), pageable);

    assertThat(orderPageFound).isNotNull();
    assertThat(orderPageFound.getSize()).isEqualTo(PAGE_SIZE);
    assertThat(orderPageFound.getTotalPages()).isEqualTo(size);
    assertThat(orderPageFound.getTotalElements()).isEqualTo(size);
    verify(uuidValidator, times(2)).validate(any(String.class));
  }

  @Test
  void shouldReturnNothingWhenGetOrdersByCompanyIdWasNotFound() {
    var order = createOrder();
    var companyId = order.getCompanyId();
    var orders = new ArrayList<Order>();
    var pageable = PageRequest.of(PAGE_NUMBER, PAGE_SIZE);
    var size = 0;
    var orderPage = new PageImpl<>(orders, pageable, size);
    when(orderService.findOrderByCompanyIdOrCustomerId(companyId, null, pageable))
        .thenReturn(orderPage);

    var orderPageFound = getOrdersByCompanyIdOrCustomerIdUseCase.execute(companyId.toString(),
        null, pageable);

    assertThat(orderPageFound).isNotNull();
    assertThat(orderPageFound.getSize()).isEqualTo(PAGE_SIZE);
    assertThat(orderPageFound.getTotalPages()).isEqualTo(size);
    assertThat(orderPageFound.getTotalElements()).isEqualTo(size);
    verify(uuidValidator, times(1)).validate(any(String.class));
  }

  @Test
  void shouldReturnNothingWhenGetOrdersByCustomerIdWasNotFound() {
    var order = createOrder();
    var customerId = order.getCustomerId();
    var orders = new ArrayList<Order>();
    var pageable = PageRequest.of(PAGE_NUMBER, PAGE_SIZE);
    var size = 0;
    var orderPage = new PageImpl<>(orders, pageable, size);
    when(orderService.findOrderByCompanyIdOrCustomerId(null, customerId, pageable))
        .thenReturn(orderPage);

    var orderPageFound = getOrdersByCompanyIdOrCustomerIdUseCase.execute(null,
        customerId.toString(), pageable);

    assertThat(orderPageFound).isNotNull();
    assertThat(orderPageFound.getSize()).isEqualTo(PAGE_SIZE);
    assertThat(orderPageFound.getTotalPages()).isEqualTo(size);
    assertThat(orderPageFound.getTotalElements()).isEqualTo(size);
    verify(uuidValidator, times(1)).validate(any(String.class));
  }
}
