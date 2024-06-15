package br.com.fiap.order.application.usecase;

import static br.com.fiap.order.domain.enums.OrderStatus.PAGO;
import static br.com.fiap.order.domain.fields.OrderFields.ORDER_ID_FIELD;
import static br.com.fiap.order.domain.messages.OrderMessages.ORDER_NOT_FOUND_WITH_ID;
import static br.com.fiap.order.shared.testdata.OrderTestData.ALTERNATIVE_COMPANY_UUID;
import static br.com.fiap.order.shared.testdata.OrderTestData.ALTERNATIVE_CUSTOMER_UUID;
import static br.com.fiap.order.shared.testdata.OrderTestData.ALTERNATIVE_PRODUCT_PRICE;
import static br.com.fiap.order.shared.testdata.OrderTestData.ALTERNATIVE_PRODUCT_QUANTITY;
import static br.com.fiap.order.shared.testdata.OrderTestData.ALTERNATIVE_PRODUCT_UUID;
import static br.com.fiap.order.shared.testdata.OrderTestData.DEFAULT_PRODUCT_PRICE;
import static br.com.fiap.order.shared.testdata.OrderTestData.DEFAULT_PRODUCT_QUANTITY;
import static br.com.fiap.order.shared.testdata.OrderTestData.DEFAULT_PRODUCT_UUID;
import static br.com.fiap.order.shared.testdata.OrderTestData.OTHER_PRODUCT_UUID;
import static br.com.fiap.order.shared.testdata.OrderTestData.createNewOrder;
import static br.com.fiap.order.shared.testdata.OrderTestData.createOrder;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import br.com.fiap.order.application.validator.OrderIsAbleToUpdateValidator;
import br.com.fiap.order.application.validator.UuidValidator;
import br.com.fiap.order.domain.entity.Order;
import br.com.fiap.order.domain.entity.OrderItem;
import br.com.fiap.order.domain.exception.NoResultException;
import br.com.fiap.order.domain.service.OrderService;
import br.com.fiap.order.infrastructure.httpclient.cep.GetCoordinatesFromCepRequest;
import br.com.fiap.order.presentation.api.dto.OrderDto;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.validation.FieldError;

@ExtendWith(MockitoExtension.class)
class UpdateOrderUseCaseTest {

  @Mock
  private OrderService orderService;
  @Mock
  private UuidValidator uuidValidator;
  @Mock
  private OrderIsAbleToUpdateValidator orderIsAbleToUpdateValidator;
  @Mock
  private GetCoordinatesFromCepRequest getCoordinatesFromCepRequest;
  @InjectMocks
  private UpdateOrderUseCase updateOrderUseCase;

  @Test
  void shouldUpdateOrderWhenAllAttributesAreCorrect() {
    var order = createOrder();
    var orderDto = new OrderDto(order);
    var orderId = order.getId();
    when(orderService.findByIdRequired(orderId)).thenReturn(order);
    order.setCompanyId(ALTERNATIVE_COMPANY_UUID);
    order.setCustomerId(ALTERNATIVE_CUSTOMER_UUID);
    order.setOrderStatus(PAGO);
    order.getOrderItems().get(0).setProductId(ALTERNATIVE_PRODUCT_UUID);
    order.getOrderItems().get(0).setQuantity(ALTERNATIVE_PRODUCT_QUANTITY);
    order.getOrderItems().get(0).setPrice(ALTERNATIVE_PRODUCT_PRICE);
    when(orderService.save(any(Order.class))).thenReturn(order);

    var orderUpdated = updateOrderUseCase.execute(orderId.toString(), orderDto);

    assertThat(orderUpdated).isNotNull();
    assertThat(orderUpdated).usingRecursiveComparison().isEqualTo(order);
    verify(uuidValidator).validate(any(String.class));
    verify(orderIsAbleToUpdateValidator).validate(any(Order.class));
    verify(getCoordinatesFromCepRequest, times(2)).request(any(String.class));
  }

  @Test
  void shouldUpdateOrderWhenAllAttributesAreCorrectWithTwoItems() {
    var order = createNewOrder();
    order.setId(UUID.randomUUID());
    order.getOrderItems().get(0).setId(UUID.randomUUID());
    var firstOrderItem = OrderItem.builder()
        .order(order)
        .productId(DEFAULT_PRODUCT_UUID)
        .quantity(DEFAULT_PRODUCT_QUANTITY)
        .price(DEFAULT_PRODUCT_PRICE)
        .build();
    order.getOrderItems().add(firstOrderItem);
    var secondOrderItem = OrderItem.builder()
        .order(order)
        .productId(OTHER_PRODUCT_UUID)
        .quantity(DEFAULT_PRODUCT_QUANTITY)
        .price(DEFAULT_PRODUCT_PRICE)
        .build();
    order.getOrderItems().add(secondOrderItem);
    var orderDto = new OrderDto(order);
    var orderId = order.getId();
    when(orderService.findByIdRequired(orderId)).thenReturn(order);
    order.setCompanyId(ALTERNATIVE_COMPANY_UUID);
    order.setCustomerId(ALTERNATIVE_CUSTOMER_UUID);
    order.setOrderStatus(PAGO);
    order.getOrderItems().get(0).setProductId(ALTERNATIVE_PRODUCT_UUID);
    order.getOrderItems().get(0).setQuantity(ALTERNATIVE_PRODUCT_QUANTITY);
    order.getOrderItems().get(0).setPrice(ALTERNATIVE_PRODUCT_PRICE);
    order.getOrderItems().get(1).setId(UUID.randomUUID());
    when(orderService.save(any(Order.class))).thenReturn(order);

    var orderUpdated = updateOrderUseCase.execute(orderId.toString(), orderDto);

    assertThat(orderUpdated).isNotNull();
    assertThat(orderUpdated).usingRecursiveComparison().isEqualTo(order);
    verify(uuidValidator).validate(any(String.class));
    verify(orderIsAbleToUpdateValidator).validate(any(Order.class));
    verify(getCoordinatesFromCepRequest, times(2)).request(any(String.class));
  }

  @Test
  void shouldThrowExceptionWhenOrderWasNotFoundById() {
    var order = createOrder();
    var orderDto = new OrderDto(order);
    var orderId = order.getId();
    when(orderService.findByIdRequired(orderId)).thenThrow(
        new NoResultException(new FieldError(this.getClass().getSimpleName(),
            ORDER_ID_FIELD, ORDER_NOT_FOUND_WITH_ID.formatted(orderId))));

    assertThatThrownBy(() -> updateOrderUseCase.execute(orderId.toString(), orderDto))
        .isInstanceOf(NoResultException.class)
        .hasMessage(ORDER_NOT_FOUND_WITH_ID.formatted(orderId));
    verify(orderIsAbleToUpdateValidator, never()).validate(any(Order.class));
    verify(getCoordinatesFromCepRequest, never()).request(any(String.class));
  }
}
