package br.com.fiap.order.application.usecase;

import static br.com.fiap.order.domain.enums.OrderStatus.PAGO;
import static br.com.fiap.order.domain.fields.OrderFields.ORDER_ID_FIELD;
import static br.com.fiap.order.domain.messages.OrderMessages.ORDER_NOT_FOUND_WITH_ID;
import static br.com.fiap.order.infrastructure.httpclient.company.fields.CompanyFields.COMPANY_ID_FIELD;
import static br.com.fiap.order.infrastructure.httpclient.company.messages.CompanyMessages.COMPANY_NOT_FOUND_WITH_ID_MESSAGE;
import static br.com.fiap.order.infrastructure.httpclient.product.fields.ProductFields.PRODUCT_ID_FIELD;
import static br.com.fiap.order.infrastructure.httpclient.product.messages.ProductMessages.PRODUCT_NOT_FOUND_WITH_ID_MESSAGE;
import static br.com.fiap.order.infrastructure.httpclient.user.fields.UserFields.USER_ID_FIELD;
import static br.com.fiap.order.infrastructure.httpclient.user.messages.UserMessages.USER_NOT_FOUND_WITH_ID_MESSAGE;
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
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import br.com.fiap.order.application.validator.CompanyExistsValidator;
import br.com.fiap.order.application.validator.CustomerExistsValidator;
import br.com.fiap.order.application.validator.OrderIsAbleToUpdateValidator;
import br.com.fiap.order.application.validator.ProductExistsValidator;
import br.com.fiap.order.application.validator.UuidValidator;
import br.com.fiap.order.domain.entity.Order;
import br.com.fiap.order.domain.entity.OrderItem;
import br.com.fiap.order.domain.exception.NoResultException;
import br.com.fiap.order.domain.service.OrderService;
import br.com.fiap.order.presentation.api.dto.OrderDto;
import java.util.List;
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
  private CompanyExistsValidator companyExistsValidator;
  @Mock
  private CustomerExistsValidator customerExistsValidator;
  @Mock
  private ProductExistsValidator productExistsValidator;
  @Mock
  private OrderIsAbleToUpdateValidator orderIsAbleToUpdateValidator;
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
    verify(companyExistsValidator).validate(order.getCompanyId().toString());
    verify(customerExistsValidator).validate(order.getCustomerId().toString());
    verify(productExistsValidator).validate(any());
    verify(orderIsAbleToUpdateValidator).validate(any(Order.class));
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
    verify(companyExistsValidator).validate(order.getCompanyId().toString());
    verify(customerExistsValidator).validate(order.getCustomerId().toString());
    verify(productExistsValidator).validate(any());
    verify(orderIsAbleToUpdateValidator).validate(any(Order.class));
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

    verify(companyExistsValidator, never()).validate(order.getCompanyId().toString());
    verify(customerExistsValidator, never()).validate(order.getCustomerId().toString());
    verify(productExistsValidator, never()).validate(any());
    verify(orderIsAbleToUpdateValidator, never()).validate(any(Order.class));
  }

  @Test
  void shouldThrowNotFoundExceptionWhenCompanyWasNotFoundByCompanyId() {
    var order = createOrder();
    var orderDto = new OrderDto(order);
    var orderId = order.getId();
    doThrow(new NoResultException(new FieldError(this.getClass().getSimpleName(), COMPANY_ID_FIELD,
        COMPANY_NOT_FOUND_WITH_ID_MESSAGE.formatted(order.getCompanyId())))).when(
        companyExistsValidator).validate(order.getCompanyId().toString());

    assertThatThrownBy(() -> updateOrderUseCase.execute(orderId.toString(), orderDto))
        .isInstanceOf(NoResultException.class)
        .hasMessage(COMPANY_NOT_FOUND_WITH_ID_MESSAGE.formatted(order.getCompanyId()));

    verify(customerExistsValidator, never()).validate(order.getCustomerId().toString());
    verify(productExistsValidator, never()).validate(any());
    verify(orderIsAbleToUpdateValidator, never()).validate(any(Order.class));
  }

  @Test
  void shouldThrowNotFoundExceptionWhenCustomerWasNotFoundByCustomerId() {
    var order = createOrder();
    var orderDto = new OrderDto(order);
    var orderId = order.getId();
    doThrow(new NoResultException(new FieldError(this.getClass().getSimpleName(), USER_ID_FIELD,
        USER_NOT_FOUND_WITH_ID_MESSAGE.formatted(order.getCustomerId())))).when(
        customerExistsValidator).validate(order.getCustomerId().toString());

    assertThatThrownBy(() -> updateOrderUseCase.execute(orderId.toString(), orderDto))
        .isInstanceOf(NoResultException.class)
        .hasMessage(USER_NOT_FOUND_WITH_ID_MESSAGE.formatted(order.getCustomerId()));

    verify(productExistsValidator, never()).validate(any());
    verify(orderIsAbleToUpdateValidator, never()).validate(any(Order.class));
  }

  @Test
  void shouldThrowNotFoundExceptionWhenProductWasNotFoundByProductId() {
    var order = createOrder();
    var orderDto = new OrderDto(order);
    var orderId = order.getId();
    var productId = order.getOrderItems().get(0).getProductId().toString();
    when(orderService.findByIdRequired(orderId)).thenReturn(order);
    doThrow(new NoResultException(new FieldError(this.getClass().getSimpleName(), PRODUCT_ID_FIELD,
        PRODUCT_NOT_FOUND_WITH_ID_MESSAGE.formatted(productId)))).when(
        productExistsValidator).validate(List.of(productId));

    assertThatThrownBy(() -> updateOrderUseCase.execute(orderId.toString(), orderDto))
        .isInstanceOf(NoResultException.class)
        .hasMessage(PRODUCT_NOT_FOUND_WITH_ID_MESSAGE.formatted(productId));
  }
}
