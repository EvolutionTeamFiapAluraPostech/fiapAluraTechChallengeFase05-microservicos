package br.com.fiap.payment.infrastructure.httpclient.order.request;

import br.com.fiap.payment.infrastructure.httpclient.order.OrderHttpClient;
import org.springframework.stereotype.Service;

@Service
public class PatchOrderPaymentByIdHttpRequest {

  private final OrderHttpClient orderHttpClient;

  public PatchOrderPaymentByIdHttpRequest(OrderHttpClient orderHttpClient) {
    this.orderHttpClient = orderHttpClient;
  }

  public void request(String id) {
    orderHttpClient.putOrderPaymentById(id);
  }
}
