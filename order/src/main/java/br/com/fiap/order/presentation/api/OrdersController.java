package br.com.fiap.order.presentation.api;

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
  private final DeleteOrderUseCase deleteOrderUseCase;
  private final ConfirmOrderPaymentUseCase confirmOrderPaymentUseCase;

  public OrdersController(CreateOrderUseCase createOrderUseCase,
      GetOrderByIdUseCase getOrderByIdUseCase,
      GetOrdersByCompanyIdOrCustomerIdUseCase getOrdersByCompanyIdOrCustomerIdUseCase,
      UpdateOrderUseCase updateOrderUseCase,
      DeleteOrderUseCase deleteOrderUseCase,
      ConfirmOrderPaymentUseCase confirmOrderPaymentUseCase) {
    this.createOrderUseCase = createOrderUseCase;
    this.getOrderByIdUseCase = getOrderByIdUseCase;
    this.getOrdersByCompanyIdOrCustomerIdUseCase = getOrdersByCompanyIdOrCustomerIdUseCase;
    this.updateOrderUseCase = updateOrderUseCase;
    this.deleteOrderUseCase = deleteOrderUseCase;
    this.confirmOrderPaymentUseCase = confirmOrderPaymentUseCase;
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

  @DeleteMapping("/{id}")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  @Override
  public void deleteOrder(@PathVariable String id) {
    deleteOrderUseCase.execute(id);
  }

  @PutMapping("/{id}/payment-confirmation")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  @Override
  public void putOrderPaymentConfirmation(@PathVariable String id) {
    confirmOrderPaymentUseCase.execute(id);
  }
}
