package br.com.fiap.payment.application.usecase;

import static br.com.fiap.payment.domain.fields.PaymentFields.PAYMENT_ORDER_ORDER_ITEMS_FIELD;
import static br.com.fiap.payment.domain.fields.PaymentFields.PAYMENT_ORDER_ORDER_STATUS_FIELD;
import static br.com.fiap.payment.domain.messages.PaymentMessages.PAYMENT_ORDER_WITHOUT_ITEMS_MESSAGE;
import static br.com.fiap.payment.domain.messages.PaymentMessages.PAYMENT_ORDER_WITH_INVALID_STATUS_MESSAGE;
import static br.com.fiap.payment.domain.messages.PaymentMessages.PAYMENT_ORDER_WITH_ITEM_WITHOUT_TOTAL_AMOUNT_MESSAGE;
import static br.com.fiap.payment.infrastructure.httpclient.company.messages.CompanyMessages.COMPANY_NOT_FOUND_WITH_ID_MESSAGE;
import static br.com.fiap.payment.infrastructure.httpclient.customer.messages.CustomerMessages.CUSTOMER_NOT_FOUND_WITH_ID_MESSAGE;
import static br.com.fiap.payment.infrastructure.httpclient.order.messages.OrderMessages.ORDER_NOT_FOUND_WITH_ID;
import static br.com.fiap.payment.shared.fields.SharedFields.UUID_FIELD;
import static br.com.fiap.payment.shared.messages.SharedMessages.UUID_INVALID;
import static br.com.fiap.payment.shared.testdata.CompanyTestData.createCompanyDto;
import static br.com.fiap.payment.shared.testdata.CustomerTestData.createCustomerDto;
import static br.com.fiap.payment.shared.testdata.OrderTestData.createOrderDto;
import static br.com.fiap.payment.shared.testdata.OrderTestData.createOrderDtoWithItemWithInvalidTotalAmount;
import static br.com.fiap.payment.shared.testdata.OrderTestData.createOrderDtoWithoutItem;
import static br.com.fiap.payment.shared.testdata.PaymentTestData.createPaymentFrom;
import static br.com.fiap.payment.shared.testdata.PaymentTestData.createPaymentSavedFrom;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import br.com.fiap.payment.application.validator.OrderStatusValidator;
import br.com.fiap.payment.application.validator.OrderWithItemValidator;
import br.com.fiap.payment.application.validator.OrderWithItemWithoutTotalAmount;
import br.com.fiap.payment.application.validator.UserFromSecurityContextIsTheSameOfOrderValidator;
import br.com.fiap.payment.domain.entity.Payment;
import br.com.fiap.payment.domain.enums.PaymentType;
import br.com.fiap.payment.domain.exception.NoResultException;
import br.com.fiap.payment.domain.exception.ValidatorException;
import br.com.fiap.payment.domain.service.PaymentService;
import br.com.fiap.payment.infrastructure.httpclient.company.request.GetCompanyByIdHttpRequest;
import br.com.fiap.payment.infrastructure.httpclient.customer.request.GetCustomerByIdRequest;
import br.com.fiap.payment.infrastructure.httpclient.order.dto.OrderDto;
import br.com.fiap.payment.infrastructure.httpclient.order.enums.OrderStatus;
import br.com.fiap.payment.infrastructure.httpclient.order.request.GetOrderByIdHttpRequest;
import br.com.fiap.payment.infrastructure.httpclient.order.request.PatchOrderPaymentByIdHttpRequest;
import br.com.fiap.payment.presentation.api.dto.PaymentInputDto;
import br.com.fiap.payment.shared.validator.UuidValidator;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.validation.FieldError;

@ExtendWith(MockitoExtension.class)
class CreatePaymentUseCaseTest {

  @Mock
  private PaymentService paymentService;
  @Mock
  private UuidValidator uuidValidator;
  @Mock
  private GetOrderByIdHttpRequest getOrderByIdHttpRequest;
  @Mock
  private OrderStatusValidator orderStatusValidator;
  @Mock
  private UserFromSecurityContextIsTheSameOfOrderValidator userFromSecurityContextIsTheSameOfOrderValidator;
  @Mock
  private GetCompanyByIdHttpRequest getCompanyByIdHttpRequest;
  @Mock
  private GetCustomerByIdRequest getCustomerByIdRequest;
  @Mock
  private OrderWithItemValidator orderWithItemValidator;
  @Mock
  private OrderWithItemWithoutTotalAmount orderWithItemWithoutTotalAmount;
  @Mock
  private PatchOrderPaymentByIdHttpRequest patchOrderPaymentByIdHttpRequest;
  @InjectMocks
  private CreatePaymentUseCase createPaymentUseCase;

  @ParameterizedTest
  @NullAndEmptySource
  @ValueSource(strings = "aB1")
  void shouldThrowValidatorExceptionWhenOrderIdIsNullOrEmptyOrInvalid(String orderId) {
    var paymentInputDto = new PaymentInputDto(orderId, PaymentType.PIX.name());
    doThrow(new ValidatorException(new FieldError(this.getClass().getSimpleName(), UUID_FIELD,
        UUID_INVALID.formatted(orderId)))).when(uuidValidator).validate(orderId);

    assertThatThrownBy(() -> createPaymentUseCase.execute(paymentInputDto))
        .isInstanceOf(ValidatorException.class)
        .hasMessage(UUID_INVALID.formatted(orderId));

    verify(userFromSecurityContextIsTheSameOfOrderValidator, never()).validate(any(OrderDto.class));
    verify(getOrderByIdHttpRequest, never()).request(paymentInputDto.orderId());
    verify(orderStatusValidator, never()).validate(any(OrderDto.class));
    verify(getCompanyByIdHttpRequest, never()).request(any(String.class));
    verify(getCustomerByIdRequest, never()).request(any(String.class));
    verify(orderWithItemValidator, never()).validate(any(OrderDto.class));
    verify(paymentService, never()).save(any(Payment.class));
    verify(patchOrderPaymentByIdHttpRequest, never()).request(any(String.class));
  }

  @Test
  void shouldThrowNotFoundExceptionWhenOrderWasNotFoundByOrderId() {
    var orderId = UUID.randomUUID();
    var paymentInputDto = new PaymentInputDto(orderId.toString(), PaymentType.PIX.name());
    when(getOrderByIdHttpRequest.request(orderId.toString()))
        .thenThrow(new NoResultException(new FieldError(this.getClass().getSimpleName(), UUID_FIELD,
            ORDER_NOT_FOUND_WITH_ID.formatted(orderId.toString()))));

    assertThatThrownBy(() -> createPaymentUseCase.execute(paymentInputDto))
        .isInstanceOf(NoResultException.class)
        .hasMessage(ORDER_NOT_FOUND_WITH_ID.formatted(orderId));

    verify(orderStatusValidator, never()).validate(any(OrderDto.class));
    verify(userFromSecurityContextIsTheSameOfOrderValidator, never()).validate(any(OrderDto.class));
    verify(getCompanyByIdHttpRequest, never()).request(any(String.class));
    verify(getCustomerByIdRequest, never()).request(any(String.class));
    verify(orderWithItemValidator, never()).validate(any(OrderDto.class));
    verify(paymentService, never()).save(any(Payment.class));
    verify(patchOrderPaymentByIdHttpRequest, never()).request(any(String.class));
  }

  @Test
  void shouldThrowValidatorExceptionWhenOrderStatusIsInvalid() {
    var orderId = UUID.randomUUID();
    var paymentInputDto = new PaymentInputDto(orderId.toString(), PaymentType.PIX.name());
    var orderDto = createOrderDto();
    when(getOrderByIdHttpRequest.request(orderId.toString())).thenReturn(orderDto);
    doThrow(new ValidatorException(
        new FieldError(this.getClass().getSimpleName(), PAYMENT_ORDER_ORDER_STATUS_FIELD,
            PAYMENT_ORDER_WITH_INVALID_STATUS_MESSAGE.formatted(orderDto.id(),
                orderDto.orderStatus(), OrderStatus.AGUARDANDO_PAGAMENTO.name())))).when(
        orderStatusValidator).validate(orderDto);

    assertThatThrownBy(() -> createPaymentUseCase.execute(paymentInputDto))
        .isInstanceOf(ValidatorException.class)
        .hasMessage(PAYMENT_ORDER_WITH_INVALID_STATUS_MESSAGE.formatted(orderDto.id(),
            orderDto.orderStatus(), OrderStatus.AGUARDANDO_PAGAMENTO.name()));

    verify(getCustomerByIdRequest, never()).request(any(String.class));
    verify(orderWithItemValidator, never()).validate(any(OrderDto.class));
    verify(paymentService, never()).save(any(Payment.class));
    verify(patchOrderPaymentByIdHttpRequest, never()).request(any(String.class));
  }

  @Test
  void shouldThrowValidatorExceptionWhenOrderCompanyWasNotFoundByOrderId() {
    var orderId = UUID.randomUUID();
    var paymentInputDto = new PaymentInputDto(orderId.toString(), PaymentType.PIX.name());
    var orderDto = createOrderDto();
    when(getOrderByIdHttpRequest.request(orderId.toString())).thenReturn(orderDto);
    when(getCompanyByIdHttpRequest.request(orderDto.companyId()))
        .thenThrow(new NoResultException(new FieldError(this.getClass().getSimpleName(), UUID_FIELD,
            COMPANY_NOT_FOUND_WITH_ID_MESSAGE.formatted(orderDto.companyId()))));

    assertThatThrownBy(() -> createPaymentUseCase.execute(paymentInputDto))
        .isInstanceOf(NoResultException.class)
        .hasMessage(COMPANY_NOT_FOUND_WITH_ID_MESSAGE.formatted(orderDto.companyId()));

    verify(getCustomerByIdRequest, never()).request(any(String.class));
    verify(orderWithItemValidator, never()).validate(any(OrderDto.class));
    verify(paymentService, never()).save(any(Payment.class));
    verify(patchOrderPaymentByIdHttpRequest, never()).request(any(String.class));
  }

  @Test
  void shouldThrowValidatorExceptionWhenOrderCustomerWasNotFoundByOrderId() {
    var orderDto = createOrderDto();
    var paymentInputDto = new PaymentInputDto(orderDto.id(), PaymentType.PIX.name());
    var companyDto = createCompanyDto(orderDto.companyId());
    when(getOrderByIdHttpRequest.request(orderDto.id())).thenReturn(orderDto);
    when(getCompanyByIdHttpRequest.request(orderDto.companyId())).thenReturn(companyDto);
    when(getCustomerByIdRequest.request(orderDto.customerId()))
        .thenThrow(new NoResultException(new FieldError(this.getClass().getSimpleName(), UUID_FIELD,
            CUSTOMER_NOT_FOUND_WITH_ID_MESSAGE.formatted(orderDto.customerId()))));

    assertThatThrownBy(() -> createPaymentUseCase.execute(paymentInputDto))
        .isInstanceOf(NoResultException.class)
        .hasMessage(CUSTOMER_NOT_FOUND_WITH_ID_MESSAGE.formatted(orderDto.customerId()));

    verify(orderWithItemValidator, never()).validate(any(OrderDto.class));
    verify(paymentService, never()).save(any(Payment.class));
    verify(patchOrderPaymentByIdHttpRequest, never()).request(any(String.class));
  }

  @Test
  void shouldThrowValidatorExceptionWhenOrderDoesNotHaveOrderItems() {
    var orderDto = createOrderDtoWithoutItem();
    var paymentInputDto = new PaymentInputDto(orderDto.id(), PaymentType.PIX.name());
    var companyDto = createCompanyDto(orderDto.companyId());
    var customerDto = createCustomerDto();
    when(getOrderByIdHttpRequest.request(orderDto.id())).thenReturn(orderDto);
    when(getCompanyByIdHttpRequest.request(orderDto.companyId())).thenReturn(companyDto);
    when(getCustomerByIdRequest.request(orderDto.customerId())).thenReturn(customerDto);
    doThrow(new ValidatorException(
        new FieldError(this.getClass().getSimpleName(), PAYMENT_ORDER_ORDER_ITEMS_FIELD,
            PAYMENT_ORDER_WITHOUT_ITEMS_MESSAGE.formatted(orderDto.id())))).when(
        orderWithItemValidator).validate(orderDto);

    assertThatThrownBy(() -> createPaymentUseCase.execute(paymentInputDto))
        .isInstanceOf(ValidatorException.class)
        .hasMessage(PAYMENT_ORDER_WITHOUT_ITEMS_MESSAGE.formatted(orderDto.id()));

    verify(paymentService, never()).save(any(Payment.class));
    verify(patchOrderPaymentByIdHttpRequest, never()).request(any(String.class));
  }

  @Test
  void shouldThrowValidatorExceptionWhenOrderTotalAmountIsInvalid() {
    var orderDto = createOrderDtoWithItemWithInvalidTotalAmount();
    var paymentInputDto = new PaymentInputDto(orderDto.id(), PaymentType.PIX.name());
    var companyDto = createCompanyDto(orderDto.companyId());
    var customerDto = createCustomerDto();
    when(getOrderByIdHttpRequest.request(orderDto.id())).thenReturn(orderDto);
    when(getCompanyByIdHttpRequest.request(orderDto.companyId())).thenReturn(companyDto);
    when(getCustomerByIdRequest.request(orderDto.customerId())).thenReturn(customerDto);
    doNothing().when(orderWithItemValidator).validate(orderDto);
    doThrow(new ValidatorException(
        new FieldError(this.getClass().getSimpleName(), PAYMENT_ORDER_ORDER_ITEMS_FIELD,
            PAYMENT_ORDER_WITH_ITEM_WITHOUT_TOTAL_AMOUNT_MESSAGE.formatted(orderDto.id())))).when(
        orderWithItemWithoutTotalAmount).validate(orderDto);

    assertThatThrownBy(() -> createPaymentUseCase.execute(paymentInputDto))
        .isInstanceOf(ValidatorException.class)
        .hasMessage(PAYMENT_ORDER_WITH_ITEM_WITHOUT_TOTAL_AMOUNT_MESSAGE.formatted(orderDto.id()));

    verify(paymentService, never()).save(any(Payment.class));
    verify(patchOrderPaymentByIdHttpRequest, never()).request(any(String.class));
  }

  @Test
  void shouldCreateOrderPaymentWhenAllBusinessRulesAreCorrect() {
    var orderDto = createOrderDtoWithItemWithInvalidTotalAmount();
    var paymentInputDto = new PaymentInputDto(orderDto.id(), PaymentType.PIX.name());
    var companyDto = createCompanyDto(orderDto.companyId());
    var customerDto = createCustomerDto();
    var paymentToSave = createPaymentFrom(paymentInputDto, orderDto, companyDto, customerDto);
    var paymentSaved = createPaymentSavedFrom(paymentInputDto, orderDto, companyDto, customerDto);
    when(getOrderByIdHttpRequest.request(orderDto.id())).thenReturn(orderDto);
    when(getCompanyByIdHttpRequest.request(orderDto.companyId())).thenReturn(companyDto);
    when(getCustomerByIdRequest.request(orderDto.customerId())).thenReturn(customerDto);
    doNothing().when(orderWithItemValidator).validate(orderDto);
    doNothing().when(orderWithItemWithoutTotalAmount).validate(orderDto);
    when(paymentService.save(paymentToSave)).thenReturn(paymentSaved);

    var payment = createPaymentUseCase.execute(paymentInputDto);

    assertThat(payment).isNotNull();
    assertThat(paymentSaved).usingRecursiveComparison().isEqualTo(payment);
  }
}
