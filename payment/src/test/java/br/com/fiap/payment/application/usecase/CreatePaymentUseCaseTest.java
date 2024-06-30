package br.com.fiap.payment.application.usecase;

import static br.com.fiap.payment.infrastructure.httpclient.company.messages.CompanyMessages.COMPANY_NOT_FOUND_WITH_ID_MESSAGE;
import static br.com.fiap.payment.infrastructure.httpclient.customer.messages.CustomerMessages.CUSTOMER_NOT_FOUND_WITH_ID_MESSAGE;
import static br.com.fiap.payment.infrastructure.httpclient.order.messages.OrderMessages.ORDER_NOT_FOUND_WITH_ID;
import static br.com.fiap.payment.shared.fields.SharedFields.UUID_FIELD;
import static br.com.fiap.payment.shared.messages.SharedMessages.UUID_INVALID;
import static br.com.fiap.payment.shared.testdata.CompanyTestData.createCompanyDto;
import static br.com.fiap.payment.shared.testdata.OrderTestData.createOrderDto;
import static br.com.fiap.payment.shared.testdata.OrderTestData.createOrderDtoWithItemWithInvalidTotalAmount;
import static br.com.fiap.payment.shared.testdata.OrderTestData.createOrderDtoWithoutItem;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import br.com.fiap.payment.application.validator.OrderWithItemValidator;
import br.com.fiap.payment.domain.entity.Payment;
import br.com.fiap.payment.domain.enums.PaymentType;
import br.com.fiap.payment.domain.exception.NoResultException;
import br.com.fiap.payment.domain.exception.ValidatorException;
import br.com.fiap.payment.domain.service.PaymentService;
import br.com.fiap.payment.infrastructure.httpclient.company.request.GetCompanyByIdHttpRequest;
import br.com.fiap.payment.infrastructure.httpclient.customer.dto.CustomerDto;
import br.com.fiap.payment.infrastructure.httpclient.customer.request.GetCustomerByIdRequest;
import br.com.fiap.payment.infrastructure.httpclient.order.dto.OrderDto;
import br.com.fiap.payment.infrastructure.httpclient.order.request.GetOrderByIdHttpRequest;
import br.com.fiap.payment.presentation.api.dto.PaymentInputDto;
import br.com.fiap.payment.shared.validator.UuidValidator;
import java.util.UUID;
import org.assertj.core.api.Assertions;
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
  private GetCompanyByIdHttpRequest getCompanyByIdHttpRequest;
  @Mock
  private GetCustomerByIdRequest getCustomerByIdRequest;
  @Mock
  private OrderWithItemValidator orderWithItemValidator;
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

    verify(getOrderByIdHttpRequest, never()).request(paymentInputDto.orderId());
    verify(getCompanyByIdHttpRequest, never()).request(any(String.class));
    verify(getCustomerByIdRequest, never()).request(any(String.class));
    verify(orderWithItemValidator, never()).validate(any(OrderDto.class));
    verify(paymentService, never()).save(any(Payment.class));
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

    verify(getCompanyByIdHttpRequest, never()).request(any(String.class));
    verify(getCustomerByIdRequest, never()).request(any(String.class));
    verify(orderWithItemValidator, never()).validate(any(OrderDto.class));
    verify(paymentService, never()).save(any(Payment.class));
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
  }

  @Test
  void shouldThrowValidatorExceptionWhenOrderCustomerWasNotFoundByOrderId() {
    var orderId = UUID.randomUUID();
    var paymentInputDto = new PaymentInputDto(orderId.toString(), PaymentType.PIX.name());
    var orderDto = createOrderDto();
    var companyDto = createCompanyDto(orderDto.companyId());
    when(getOrderByIdHttpRequest.request(orderId.toString())).thenReturn(orderDto);
    when(getCompanyByIdHttpRequest.request(orderDto.companyId())).thenReturn(companyDto);
    when(getCustomerByIdRequest.request(orderDto.customerId()))
        .thenThrow(new NoResultException(new FieldError(this.getClass().getSimpleName(), UUID_FIELD,
            CUSTOMER_NOT_FOUND_WITH_ID_MESSAGE.formatted(orderDto.customerId()))));

    assertThatThrownBy(() -> createPaymentUseCase.execute(paymentInputDto))
        .isInstanceOf(NoResultException.class)
        .hasMessage(CUSTOMER_NOT_FOUND_WITH_ID_MESSAGE.formatted(orderDto.customerId()));

    verify(orderWithItemValidator, never()).validate(any(OrderDto.class));
    verify(paymentService, never()).save(any(Payment.class));
  }

  @Test
  void shouldThrowValidatorExceptionWhenOrderDoesNotHaveOrderItems() {
    var orderId = UUID.randomUUID();
    var paymentInputDto = new PaymentInputDto(orderId.toString(), PaymentType.PIX.name());
    var orderDto = createOrderDtoWithoutItem();
    var companyDto = createCompanyDto(orderDto.companyId());
    var customerDto = new CustomerDto();
    when(getOrderByIdHttpRequest.request(orderId.toString())).thenReturn(orderDto);
    when(getCompanyByIdHttpRequest.request(orderDto.companyId())).thenReturn(companyDto);
    when(getCustomerByIdRequest.request(orderDto.customerId())).thenReturn(customerDto);
    doThrow(new ValidatorException(new FieldError(this.getClass().getSimpleName(), UUID_FIELD,
        UUID_INVALID.formatted(orderId)))).when(orderWithItemValidator).validate(orderDto);

    assertThatThrownBy(() -> createPaymentUseCase.execute(paymentInputDto))
        .isInstanceOf(NoResultException.class)
        .hasMessage(CUSTOMER_NOT_FOUND_WITH_ID_MESSAGE.formatted(orderDto.customerId()));

    verify(paymentService, never()).save(any(Payment.class));
  }

  @ParameterizedTest
  @NullAndEmptySource
  @ValueSource(strings = {"0", "-1"})
  void shouldThrowValidatorExceptionWhenOrderTotalAmountIsNullOrEmptyOrZeroOrNegativeValue(
      String totalAmount) {
    var orderId = UUID.randomUUID();
    var paymentInputDto = new PaymentInputDto(orderId.toString(), PaymentType.PIX.name());
    var orderDto = createOrderDtoWithItemWithInvalidTotalAmount();
    var companyDto = createCompanyDto(orderDto.companyId());
    when(getOrderByIdHttpRequest.request(orderId.toString())).thenReturn(orderDto);
    when(getCompanyByIdHttpRequest.request(orderDto.companyId())).thenReturn(companyDto);
    when(getCustomerByIdRequest.request(orderDto.customerId()))
        .thenThrow(new NoResultException(new FieldError(this.getClass().getSimpleName(), UUID_FIELD,
            CUSTOMER_NOT_FOUND_WITH_ID_MESSAGE.formatted(orderDto.customerId()))));

    assertThatThrownBy(() -> createPaymentUseCase.execute(paymentInputDto))
        .isInstanceOf(NoResultException.class)
        .hasMessage(CUSTOMER_NOT_FOUND_WITH_ID_MESSAGE.formatted(orderDto.customerId()));

    verify(paymentService, never()).save(any(Payment.class));
  }

  @ParameterizedTest
  @NullAndEmptySource
  @ValueSource(strings = "aB1")
  void shouldThrowValidatorExceptionWhenPaymentTypeIsNullOrEmptyOrInvalid(String paymentType) {
    Assertions.fail("shouldThrowValidatorExceptionWhenPaymentTypeIsNullOrEmptyOrInvalid");
  }

  @Test
  void shouldCreateOrderPaymentWhenAllBusinessRulesAreCorrect() {
    Assertions.fail("shouldCreateOrderPaymentWhenAllBusinessRulesAreCorrect");
  }
}
