package br.com.fiap.payment.infrastructure.httpclient.order.request;

import static br.com.fiap.payment.infrastructure.httpclient.order.fields.OrderFields.ORDER_ID_FIELD;
import static br.com.fiap.payment.infrastructure.httpclient.order.messages.OrderMessages.ORDER_NOT_FOUND_WITH_ID;

import br.com.fiap.payment.infrastructure.httpclient.order.OrderHttpClient;
import br.com.fiap.payment.infrastructure.httpclient.order.dto.OrderDto;
import br.com.fiap.payment.infrastructure.httpclient.validator.ResponseEntityValidator;
import org.springframework.stereotype.Service;

@Service
public class GetOrderByIdHttpRequest {

  private final OrderHttpClient orderHttpClient;
  private final ResponseEntityValidator responseEntityValidator;

  public GetOrderByIdHttpRequest(OrderHttpClient orderHttpClient,
      ResponseEntityValidator responseEntityValidator) {
    this.orderHttpClient = orderHttpClient;
    this.responseEntityValidator = responseEntityValidator;
  }

  public OrderDto request(String id) {
    var responseEntityOrderDto = orderHttpClient.getOrderById(id);
    responseEntityValidator.validate(id, responseEntityOrderDto, ORDER_ID_FIELD,
        ORDER_NOT_FOUND_WITH_ID);
    return responseEntityOrderDto.getBody();
  }
}
