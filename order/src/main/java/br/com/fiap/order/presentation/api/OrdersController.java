package br.com.fiap.order.presentation.api;

import br.com.fiap.order.application.usecase.AwaitOrderDeliveryUseCase;
import br.com.fiap.order.application.usecase.ConfirmOrderDeliveryUseCase;
import br.com.fiap.order.application.usecase.ConfirmOrderPaymentUseCase;
import br.com.fiap.order.application.usecase.CreateOrderUseCase;
import br.com.fiap.order.application.usecase.DeleteOrderUseCase;
import br.com.fiap.order.application.usecase.GetOrderByIdUseCase;
import br.com.fiap.order.application.usecase.GetOrdersByCompanyIdOrCustomerIdUseCase;
import br.com.fiap.order.application.usecase.UpdateOrderUseCase;
import br.com.fiap.order.presentation.api.dto.OrderDto;
import br.com.fiap.order.presentation.api.dto.OrderFilter;
import br.com.fiap.order.presentation.api.dto.OrderInputDto;
import br.com.fiap.order.presentation.api.dto.OrderOutputDto;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/orders")
public class OrdersController implements OrdersApi {

  private final CreateOrderUseCase createOrderUseCase;
  private final GetOrderByIdUseCase getOrderByIdUseCase;
  private final GetOrdersByCompanyIdOrCustomerIdUseCase getOrdersByCompanyIdOrCustomerIdUseCase;
  private final UpdateOrderUseCase updateOrderUseCase;
  private final ConfirmOrderPaymentUseCase confirmOrderPaymentUseCase;
  private final AwaitOrderDeliveryUseCase awaitOrderDeliveryUseCase;
  private final ConfirmOrderDeliveryUseCase confirmOrderDeliveryUseCase;
  private final DeleteOrderUseCase deleteOrderUseCase;

  public OrdersController(CreateOrderUseCase createOrderUseCase,
      GetOrderByIdUseCase getOrderByIdUseCase,
      GetOrdersByCompanyIdOrCustomerIdUseCase getOrdersByCompanyIdOrCustomerIdUseCase,
      UpdateOrderUseCase updateOrderUseCase, ConfirmOrderPaymentUseCase confirmOrderPaymentUseCase,
      AwaitOrderDeliveryUseCase awaitOrderDeliveryUseCase,
      ConfirmOrderDeliveryUseCase confirmOrderDeliveryUseCase,
      DeleteOrderUseCase deleteOrderUseCase) {
    this.createOrderUseCase = createOrderUseCase;
    this.getOrderByIdUseCase = getOrderByIdUseCase;
    this.getOrdersByCompanyIdOrCustomerIdUseCase = getOrdersByCompanyIdOrCustomerIdUseCase;
    this.updateOrderUseCase = updateOrderUseCase;
    this.confirmOrderPaymentUseCase = confirmOrderPaymentUseCase;
    this.awaitOrderDeliveryUseCase = awaitOrderDeliveryUseCase;
    this.confirmOrderDeliveryUseCase = confirmOrderDeliveryUseCase;
    this.deleteOrderUseCase = deleteOrderUseCase;
  }

  @PostMapping
  @ResponseStatus(HttpStatus.CREATED)
  @Override
  public OrderDto postOrder(@RequestBody @Valid OrderInputDto orderInputDto) {
    var orderCreated = createOrderUseCase.execute(orderInputDto);
    return OrderDto.from(orderCreated);
  }

  @GetMapping("/{id}")
  @ResponseStatus(HttpStatus.OK)
  @Override
  public OrderDto getOrderById(@PathVariable String id) {
    var order = getOrderByIdUseCase.execute(id);
    return OrderDto.from(order);
  }

  @GetMapping("/company-customer")
  @ResponseStatus(HttpStatus.OK)
  @Override
  public Page<OrderDto> getOrdersByCompanyIdOrCustomerId(OrderFilter orderFilter,
      @PageableDefault Pageable pageable) {
    var ordersPage = getOrdersByCompanyIdOrCustomerIdUseCase.execute(orderFilter.companyId(),
        orderFilter.customerId(), pageable);
    return !ordersPage.getContent().isEmpty() ? OrderDto.toPage(ordersPage) : Page.empty();
  }

  @PutMapping("/{id}")
  @ResponseStatus(HttpStatus.CREATED)
  @Override
  public OrderOutputDto putOrder(@PathVariable String id, @RequestBody @Valid OrderDto orderDto) {
    var order = updateOrderUseCase.execute(id, orderDto);
    return OrderOutputDto.from(order);
  }

  @PatchMapping("/{id}/payment-confirmation")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  @Override
  public void patchOrderPaymentConfirmation(@PathVariable String id) {
    confirmOrderPaymentUseCase.execute(id);
  }

  @PutMapping("/{id}/awaiting-delivery")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  @Override
  public void putOrderAwaitingDelivery(@PathVariable String id) {
    awaitOrderDeliveryUseCase.execute(id);
  }

  @PutMapping("/{id}/delivery-confirmation")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  @Override
  public void putOrderDeliveryConfirmation(@PathVariable String id) {
    confirmOrderDeliveryUseCase.execute(id);
  }

  @DeleteMapping("/{id}")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  @Override
  public void deleteOrder(@PathVariable String id) {
    deleteOrderUseCase.execute(id);
  }
}
